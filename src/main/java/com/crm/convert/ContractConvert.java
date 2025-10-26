package com.crm.convert;

import com.crm.entity.Contract;
import com.crm.entity.ContractProduct;
import com.crm.vo.ContractVO;
import com.crm.vo.ProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContractConvert {
    ContractConvert INSTANCE = Mappers.getMapper(ContractConvert.class);

    Contract toContract(ContractVO contractVO);

    ProductVO toProductVO(ContractProduct product);

    List<ProductVO> toProductVOList(List<ContractProduct> productList);
}
