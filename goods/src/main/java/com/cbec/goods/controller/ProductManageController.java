package com.cbec.goods.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cbec.common.exception.BizException;
import com.cbec.common.rest.Result;
import com.cbec.common.util.UserUtil;
import com.cbec.goods.dao.entity.ProductEntity;
import com.cbec.goods.dao.entity.UploadRecordEntity;
import com.cbec.goods.feign.PlatformAccountApiFeign;
import com.cbec.goods.model.UploadProductOption;
import com.cbec.goods.service.ProductCollectService;
import com.cbec.goods.service.ProductService;
import com.cbec.goods.service.ProductUploadService;
import com.cbec.goods.service.UploadRecordService;
import com.cbec.internal.api.auth.PlatformAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ProductService productService;

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

    @GetMapping("/list_product")
    public Result<List<ProductEntity>> listProduct(@RequestParam(defaultValue = "1") Integer pageNo,
                                                   @RequestParam(defaultValue = "100") Integer pageSize){
        var accountList = platformAccountApiFeign.listPlatformAccountByUser(UserUtil.getUserName());
        if (CollectionUtils.isEmpty(accountList)){
            return Result.ok();
        }

        var userAccount = accountList.stream().map(PlatformAccountDTO::getPlatformUser).collect(Collectors.toList());
        var page = productService.selectPage(new Page<>(pageNo, pageSize),
                new EntityWrapper<ProductEntity>().in("platform_account", userAccount));
        return Result.ok(page.getRecords()).total(page.getTotal());
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
