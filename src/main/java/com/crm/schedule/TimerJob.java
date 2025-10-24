package com.crm.schedule;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.crm.service.ProductService;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-24 15:36
 **/
@Component
@AllArgsConstructor
public class TimerJob {

    private final ProductService productService;

    /**
     * 定时任务，每一分钟走一次更新
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void batchUpdateProductState() {
        productService.batchUpdateProductState();
    }
}
