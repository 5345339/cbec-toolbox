package com.github.lzk90s.cbec.goods.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.lzk90s.cbec.common.exception.BizException;
import com.github.lzk90s.cbec.common.rest.Result;
import com.github.lzk90s.cbec.common.util.UserUtil;
import com.github.lzk90s.cbec.goods.dao.entity.UploadRecordEntity;
import com.github.lzk90s.cbec.goods.feign.PlatformAccountApiFeign;
import com.github.lzk90s.cbec.goods.model.UploadProductOption;
import com.github.lzk90s.cbec.goods.service.ProductCollectService;
import com.github.lzk90s.cbec.goods.service.ProductUploadService;
import com.github.lzk90s.cbec.goods.service.UploadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/product_manage")
public class ProductManageController {
    @Autowired
    private ProductCollectService productCollectService;
    @Autowired
    private ProductUploadService productUploadService;
    @Autowired
    private PlatformAccountApiFeign platformAccountApiFeign;
    @Autowired
    private UploadRecordService uploadRecordService;

    @GetMapping("/sync_product")
    public Result syncProduct(@RequestParam String platformAccount,
                              @RequestParam(required = false, defaultValue = "2000-01-01 00:00:00") String startTime,
                              @RequestParam(required = false) String endTime) {
        var account = platformAccountApiFeign.getUserPlatformAccount(UserUtil.getUserName(), platformAccount);
        if (null == account) {
            throw new BizException("Platform account " + platformAccount + " not exist for user");
        }
        if (StringUtils.isEmpty(endTime)) {
            endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        productCollectService.asyncCollect(account, startTime, endTime);
        return Result.ok();
    }

    @PostMapping("/upload_product")
    public Result uploadProduct(@RequestBody @Validated UploadProductOption option) {
        var account = platformAccountApiFeign.getUserPlatformAccount(UserUtil.getUserName(),
                option.getDstPlatformAccount());
        if (null == account) {
            throw new BizException("Platform account " + option.getDstPlatformAccount() + " not exist for user");
        }
        productUploadService.upload(account, option.getProductIdList());
        return Result.ok();
    }

    @GetMapping("/get_upload_status")
    public Result<List<UploadRecordEntity>> getUploadStatus(@RequestParam String platformAccount) {
        return Result.ok(uploadRecordService.selectList(new EntityWrapper<UploadRecordEntity>()
                .eq("platform_account", platformAccount)));
    }
}
