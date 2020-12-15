package com.github.lzk90s.cbec.goods.task;

import com.github.lzk90s.cbec.common.util.UserUtil;
import com.github.lzk90s.cbec.goods.feign.PlatformAccountApiFeign;
import com.github.lzk90s.cbec.goods.service.ProductCollectService;
import com.github.lzk90s.cbec.goods.service.ProductUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
@RequestMapping("/tasks/product_upload_status_task")
@ConditionalOnProperty(prefix = "tasks", name = "productUploadStatusTask.enable", havingValue = "true", matchIfMissing = false)
public class ProductUploadStatusTask {
    @Autowired
    private ProductCollectService productCollectService;
    @Autowired
    private ProductUploadService productUploadService;
    @Autowired
    private PlatformAccountApiFeign platformAccountApiFeign;

    @GetMapping("/execute")
    @Scheduled(fixedDelay = 5 * 60 * 60 * 1000)
    public void execute() {
        log.info("Execute product upload status task");
        checkUploadStatus();
    }

    void checkUploadStatus() {
        var accountList = platformAccountApiFeign.listPlatformAccountByUser(UserUtil.getUserName());
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }
        accountList.forEach(account -> productUploadService.checkUploadStatus(account));
    }
}
