package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.ContractConvert;
import com.crm.entity.Contract;
import com.crm.entity.ContractProduct;
import com.crm.entity.Customer;
import com.crm.entity.Product;
import com.crm.mapper.ContractMapper;
import com.crm.mapper.ContractProductMapper;
import com.crm.mapper.ProductMapper;
import com.crm.query.ContractQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.ContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.vo.ContractVO;
import com.crm.vo.ProductVO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.crm.utils.NumberUtils.generateContractNumber;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
@AllArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {
    private final ProductMapper productMapper;
    private final ContractProductMapper contractProductMapper;

    @Override
    public PageResult<ContractVO> getPage(ContractQuery query) {
        Page<ContractVO> page = new Page<>(query.getPage(), query.getLimit());
//        条件查询
        MPJLambdaWrapper<Contract> wrapper = new MPJLambdaWrapper<>();
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Contract::getName, query.getName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Contract::getStatus, query.getStatus());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(Contract::getCustomerId, query.getCustomerId());
        }
        if (StringUtils.isNotBlank(query.getNumber())) {
            wrapper.like(Contract::getNumber, query.getNumber());
        }
        // 只查询目前登录的员工签署的合同信息
        Integer managerId = SecurityUser.getManagerId();
        wrapper.selectAll(Contract.class)
                .selectAs(Customer::getName, ContractVO::getCustomerName)
                .leftJoin(Customer.class, Customer::getId, Contract::getCustomerId)
                .eq(Contract::getOwnerId, managerId).orderByDesc(Contract::getCreateTime);
        Page<ContractVO> result = baseMapper.selectJoinPage(page, ContractVO.class, wrapper);
//        查询合同签署的商品信息
        if (!result.getRecords().isEmpty()) {
            result.getRecords().forEach(contractVO -> {
                List<ContractProduct> contractProducts = contractProductMapper.selectList(new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractVO.getId()));
                contractVO.setProducts(ContractConvert.INSTANCE.toProductVOList(contractProducts));
            });
        }
        return new PageResult<>(result.getRecords(), page.getTotal());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(ContractVO contractVO) {

        boolean isNew = contractVO.getId() == null;

        // 校验合同名称重复
        if (isNew && baseMapper.exists(new LambdaQueryWrapper<Contract>().eq(Contract::getName, contractVO.getName()))) {
            throw new ServerException("合同名称已存在，请勿重复添加");
        }

        // 转换并保存合同
        Contract contract = ContractConvert.INSTANCE.toContract(contractVO);
        contract.setCreaterId(SecurityUser.getManagerId());
        contract.setOwnerId(SecurityUser.getManagerId());
        if (isNew) {
            contract.setNumber(generateContractNumber());
            baseMapper.insert(contract);
        } else {
            Contract dbContract = baseMapper.selectById(contract.getId());
            if (dbContract == null) throw new ServerException("合同不存在");
            if (dbContract.getStatus() == 1) throw new ServerException("该合同已审核通过，请勿修改");
            baseMapper.updateById(contract);
        }

        // 处理合同商品明细
        handleContractProducts(contract.getId(), contractVO.getProducts());

    }

    private void handleContractProducts(Integer contractId, List<ProductVO> newProductList) {
        if (newProductList == null) return;

        List<ContractProduct> oldProducts = contractProductMapper.selectList(
                new LambdaQueryWrapper<ContractProduct>().eq(ContractProduct::getCId, contractId)
        );

        // === 1. 新增商品 ===
        List<ProductVO> newAdded = newProductList.stream()
                .filter(np -> oldProducts.stream().noneMatch(op -> op.getPId().equals(np.getPId())))
                .toList();
        for (ProductVO p : newAdded) {
            Product product = checkAndGetProduct(p.getPId(), p.getCount());
            decreaseStock(product, p.getCount());
            ContractProduct cp = buildContractProduct(contractId, product, p.getCount());
            contractProductMapper.insert(cp);
        }

        // === 2. 修改数量 ===
        List<ProductVO> changed = newProductList.stream()
                .filter(np -> oldProducts.stream()
                        .anyMatch(op -> op.getPId().equals(np.getPId()) && !op.getCount().equals(np.getCount())))
                .toList();
        for (ProductVO p : changed) {
            ContractProduct old = oldProducts.stream()
                    .filter(op -> op.getPId().equals(p.getPId()))
                    .findFirst().orElseThrow();

            Product product = checkAndGetProduct(p.getPId(), 0);
            int diff = p.getCount() - old.getCount();

            // 库存调整
            if (diff > 0) decreaseStock(product, diff);
            else if (diff < 0) increaseStock(product, -diff);

            // 更新合同商品
            old.setCount(p.getCount());
            old.setPrice(product.getPrice());
            old.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(p.getCount())));
            contractProductMapper.updateById(old);
        }

        // === 3. 删除商品 ===
        List<ContractProduct> removed = oldProducts.stream()
                .filter(op -> newProductList.stream().noneMatch(np -> np.getPId().equals(op.getPId())))
                .toList();
        for (ContractProduct rm : removed) {
            Product product = productMapper.selectById(rm.getPId());
            if (product != null) increaseStock(product, rm.getCount());
            contractProductMapper.deleteById(rm.getId());
        }
    }


    private Product checkAndGetProduct(Integer productId, int needCount) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new ServerException("商品不存在");
        if (needCount > 0 && product.getStock() < needCount) {
            throw new ServerException("商品库存不足");
        }
        return product;
    }

    private void decreaseStock(Product product, int count) {
        product.setStock(product.getStock() - count);
        product.setSales(product.getSales() + count);
        productMapper.updateById(product);
    }

    private void increaseStock(Product product, int count) {
        product.setStock(product.getStock() + count);
        product.setSales(product.getSales() - count);
        productMapper.updateById(product);
    }

    private ContractProduct buildContractProduct(Integer contractId, Product product, int count) {
        ContractProduct cp = new ContractProduct();
        cp.setCId(contractId);
        cp.setPId(product.getId());
        cp.setPName(product.getName());
        cp.setCount(count);
        cp.setPrice(product.getPrice());
        cp.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(count)));
        return cp;
    }


}
