package com.crm.query;


import com.crm.common.model.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-21 15:47
 **/
@Data
public class ProductQuery extends Query {
    @Schema(description = "商品名称")
    private String name;
    @Schema(description = "商品状态")
    private Integer status;
}
