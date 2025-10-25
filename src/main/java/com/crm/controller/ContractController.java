package com.crm.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.query.ContractQuery;
import com.crm.service.ContractService;
import com.crm.vo.ContractVO;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Tag(name = "合同管理")
@RestController
@RequestMapping("contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping("page")
    @Operation(summary = "分页查询签署的合同列表")
    public Result<PageResult<ContractVO>> getPage(@RequestBody @Validated ContractQuery query) {
        return Result.ok(contractService.getPage(query));
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增/修改合同信息")
    public Result saveOrUpdate(@RequestBody @Validated ContractVO customerVO) {
        contractService.saveOrUpdate(customerVO);
        return Result.ok();
    }

}
