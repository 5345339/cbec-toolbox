package com.cbec.goods.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cbec.goods.dao.entity.ProductEntity;
import com.cbec.goods.dao.entity.UploadRecordEntity;
import com.cbec.goods.feign.PlatformAccountApiFeign;
import com.cbec.goods.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RestController
@RequestMapping("/tasks/product_upload_task")
@ConditionalOnProperty(prefix = "tasks", name = "productUploadTask.enable", havingValue = "true", matchIfMissing = false)
public class ProductUploadTask {
    @Autowired
    private ProductCollectService productCollectService;
    @Autowired
    private ProductUploadService productUploadService;
    @Autowired
    private PlatformAccountApiFeign platformAccountApiFeign;
    @Autowired
    private ProductService productService;
    @Autowired
    private UploadRecordService uploadRecordService;
    @Autowired
    private CloneProductService cloneProductService;

    @Value("${maxUploadProductPerTime:10}")
    private int maxUploadProductPerTime;

    @GetMapping("/clone_product")
    @Scheduled(cron = "0 0 8 * * ?")
    public void cloneProduct() {
        log.info("Clone product");

        var cloneTaskList = cloneProductService.selectList(null);
        if (CollectionUtils.isEmpty(cloneTaskList)) {
            return;
        }

        var date = getDateRangeBeforeToday(1);
        cloneTaskList.forEach(s -> cloneProductFromPlatformAccount(s.getUserName(), s.getSrcPlatformAccount(),
                date.getKey(), date.getValue()));
    }

    @GetMapping("/clone_product_ex")
    public void cloneProductEx(@RequestParam(defaultValue = "1") Integer diffDays) {
        log.info("Clone product");

        var cloneTaskList = cloneProductService.selectList(null);
        if (CollectionUtils.isEmpty(cloneTaskList)) {
            return;
        }

        var date = getDateRangeBeforeToday(diffDays);
        cloneTaskList.forEach(s -> cloneProductFromPlatformAccount(s.getUserName(), s.getSrcPlatformAccount(),
                date.getKey(), date.getValue()));
    }

    @GetMapping("/upload_product")
    @Scheduled(fixedDelay = 2 * 60 * 1000)
    public void uploadProduct() {
        log.info("Upload product");

        var cloneTaskList = cloneProductService.selectList(null);
        if (CollectionUtils.isEmpty(cloneTaskList)) {
            return;
        }

        var date = getDateRangeBeforeToday(0);
        cloneTaskList.forEach(s -> uploadProductToPlatformAccount(s.getUserName(), s.getSrcPlatformAccount(),
                s.getDstPlatformAccount(), date.getKey(), date.getValue()));
    }

    @GetMapping("/check_upload_status")
    @Scheduled(fixedDelay = 2 * 60 * 1000)
    public void checkUploadStatus() {
        log.info("Check upload status");

        var accountList = platformAccountApiFeign.listAllUserPlatformAccount();
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }

        accountList.forEach(account -> productUploadService.checkUploadStatus(account));
        accountList.forEach(account -> productUploadService.enableProductSale(account));
    }

    private void cloneProductFromPlatformAccount(String user, String srcPlatformAccount, String startTime, String endTime) {
        var srcAccount = platformAccountApiFeign.getUserPlatformAccount(user, srcPlatformAccount);
        productCollectService.asyncCollect(srcAccount, startTime, endTime);
    }

    private void uploadProductToPlatformAccount(String user, String srcPlatformAccount, String dstPlatformAccount, String startTime, String endTime) {
        var productList = productService.selectList(new EntityWrapper<ProductEntity>()
                .eq("platform_account", srcPlatformAccount)
                .between("add_time", startTime, endTime))
                .stream()
                .filter(s -> uploadRecordService.selectCount(new EntityWrapper<UploadRecordEntity>()
                        .eq("original_product_id", s.getId())
                        .eq("platform_account", dstPlatformAccount)) == 0)
                .limit(maxUploadProductPerTime)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productList)) {
            log.info("No product to upload");
            return;
        }

        var dstAccount = platformAccountApiFeign.getUserPlatformAccount(user, dstPlatformAccount);
        var productIdList = productList.stream().map(ProductEntity::getId).collect(Collectors.toList());
        productUploadService.upload(dstAccount, productIdList);
    }

    Map.Entry<String, String> getDateRangeBeforeToday(int diffDays) {
        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var date = LocalDateTime.now().minusDays(diffDays).format(dateFormatter);
        String startTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        return Map.entry(startTime, endTime);
    }
}
