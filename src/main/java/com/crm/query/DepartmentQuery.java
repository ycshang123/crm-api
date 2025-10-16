package com.crm.query;


import com.crm.common.model.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-12 13:34
 **/

@Data
public class DepartmentQuery extends Query {
    @Schema(description = "部门名称")
    private String name;
}
