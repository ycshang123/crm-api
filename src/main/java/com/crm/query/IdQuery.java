package com.crm.query;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-13 21:13
 **/
@Data
public class IdQuery {

    @NotNull(message = "id不能为空")
    private Integer id;
}
