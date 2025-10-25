package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Contract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.ContractQuery;
import com.crm.vo.ContractVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ContractService extends IService<Contract> {
    /**
     * 合同列表-分页
     *
     * @param query
     * @return
     */
    PageResult<ContractVO> getPage(ContractQuery query);

    /**
     * 新增/修改合同信息
     *
     * @param customerVO
     */
    void saveOrUpdate(ContractVO customerVO);

}
