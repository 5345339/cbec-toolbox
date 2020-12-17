package com.cbec.goods.task;

import com.cbec.goods.service.GoodsGrabService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
@RequestMapping("/tasks/goods_spider_task")
@ConditionalOnProperty(prefix = "tasks", name = "goodsSpiderTask.enable", havingValue = "true", matchIfMissing = false)
public class GoodsSpiderTask {
    @Autowired
    private GoodsGrabService goodsGrabService;

    @GetMapping("/execute")
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void execute() {
        log.info("Execute goods spider task");
        goodsGrabService.grabGoods();
    }
}
