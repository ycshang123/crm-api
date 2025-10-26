package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.ProductQuery;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ProductService extends IService<Product> {
    /**
     * 商品列表-分页
     *
     * @param query
     * @return
     */
    PageResult<Product> getPage(ProductQuery query);

    /**
     * 商品新增/修改
     *
     * @param product
     */
    void saveOrEdit(Product product);

    /**
     * 批量更新商品状态
     */
    void batchUpdateProductState();

}
