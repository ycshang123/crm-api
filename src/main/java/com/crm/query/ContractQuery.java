package com.crm.query;


import com.crm.common.model.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-24 17:16
 **/
@Data
public class ContractQuery extends Query {
    @Schema(description = "合同名称")
    private String name;
    @Schema(description = "客户id")
    private Integer customerId;
    @Schema(description = "合同编号")
    private String number;
    @Schema(description = "合同状态 0-初始化，1-审核通过，2-审核未通过")
    private Integer status;
}
