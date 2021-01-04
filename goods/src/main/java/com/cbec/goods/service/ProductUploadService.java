package com.cbec.goods.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cbec.common.exception.BizException;
import com.cbec.goods.dao.entity.ProductEntity;
import com.cbec.goods.dao.entity.ProductResourceEntity;
import com.cbec.goods.dao.entity.ProductSkuEntity;
import com.cbec.goods.dao.entity.UploadRecordEntity;
import com.cbec.goods.feign.GoodsSpiderApiFeign;
import com.cbec.internal.api.auth.PlatformAccountDTO;
import com.cbec.internal.api.spider.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductUploadService {
    @Value("${maxUploadSkuPerTime:300}")
    private int maxUploadSkuPerTime;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private ProductResourceService productResourceService;
    @Autowired
    private GoodsSpiderApiFeign goodsSpiderApiFeign;
    @Autowired
    private UploadRecordService uploadRecordService;

    @Transactional(rollbackFor = Exception.class)
    public void upload(PlatformAccountDTO platformAccount, List<String> productIdList) {
        var productList = productIdList.stream()
                .filter(productId -> {
                    // 过滤掉已经上传过的
                    int count = uploadRecordService.selectCount(new EntityWrapper<UploadRecordEntity>().eq("platform_account",
                            platformAccount.getPlatformUser()).eq("original_product_id", productId));
                    return count == 0;
                })
                .map(productId -> {
                    ProductDTO dto = ProductEntity.getConverter().doForward(productService.selectById(productId));
                    dto.setSkuList(productSkuService.selectList(new EntityWrapper<ProductSkuEntity>()
                            .eq("product_id", productId)).stream()
                            .map(sku -> ProductSkuEntity.getConverter().doForward(sku))
                            .collect(Collectors.toList()));
                    dto.setImageList(productResourceService.selectList(new EntityWrapper<ProductResourceEntity>()
                            .eq("product_id", productId)).stream()
                            .map(img -> ProductResourceEntity.getConverter().doForward(img))
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());

        int skuNum = productList.stream().mapToInt(s -> s.getSkuList().size()).sum();
        if (skuNum > maxUploadSkuPerTime) {
            throw new BizException("当前上传sku数量" + skuNum + ", 超出单次上传sku数最大限制" + maxUploadSkuPerTime);
        }

        var uploadId = goodsSpiderApiFeign.uploadProduct(platformAccount.getPlatform(),
                platformAccount.getApiToken(), productList);
        if (StringUtils.isEmpty(uploadId)) {
            throw new BizException("上传id为空");
        }

        var uploadRecordList = productList.stream()
                .map(product -> UploadRecordEntity.builder()
                        .originalProductId(product.getId())
                        .originalParentSku(product.getParentSku())
                        .platformAccount(platformAccount.getPlatformUser())
                        .status(UploadStatusEnum.PREPARE.getStatus())
                        .uploadId(uploadId)
                        .build())
                .collect(Collectors.toList());
        uploadRecordService.insertBatch(uploadRecordList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkUploadStatus(PlatformAccountDTO platformAccount) {
        var uploadIdList = uploadRecordService.selectList(new EntityWrapper<UploadRecordEntity>()
                .eq("platform_account", platformAccount.getPlatformUser())
                .eq("status", UploadStatusEnum.PREPARE.getStatus())
                .groupBy("upload_id"))
                .stream()
                .map(UploadRecordEntity::getUploadId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(uploadIdList)) {
            return;
        }

        uploadIdList.forEach(r -> {
            var statusDTO = goodsSpiderApiFeign.getUploadStatus(platformAccount.getPlatform(),
                    platformAccount.getApiToken(), r);
            if (null == statusDTO || CollectionUtils.isEmpty(statusDTO.getProductList())) {
                return;
            }

            statusDTO.getProductList().stream()
                    .map(s -> UploadRecordEntity.builder()
                            .status(statusDTO.getStatus())
                            .message(statusDTO.getMessage())
                            .newProductId(s.getProductId())
                            .originalParentSku(s.getParentSku()) // parent sku is same as original parent sku
                            .build())
                    .forEach(s -> uploadRecordService.update(s, new EntityWrapper<UploadRecordEntity>()
                            .eq("platform_account", platformAccount.getPlatformUser())
                            .eq("upload_id", r)
                            .eq("original_parent_sku", s.getOriginalParentSku())));
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void enableProductSale(PlatformAccountDTO platformAccount) {
        var uploadedProductList = uploadRecordService.selectList(new EntityWrapper<UploadRecordEntity>()
                .eq("platform_account", platformAccount.getPlatformUser())
                .eq("status", UploadStatusEnum.UPLOADED.getStatus()));
        if (CollectionUtils.isEmpty(uploadedProductList)) {
            log.warn("No product need to enable sale");
            return;
        }

        String status;
        String message;

        try {
            goodsSpiderApiFeign.enableProductSale(platformAccount.getPlatform(), platformAccount.getApiToken(),
                    uploadedProductList.stream().map(UploadRecordEntity::getNewProductId).collect(Collectors.toList()));
            status = UploadStatusEnum.IN_SALE.getStatus();
            message = "已上架";
        } catch (Exception e) {
            status = UploadStatusEnum.NOT_IN_SALE.getStatus();
            message = e.getMessage();
        }

        String finalStatus = status;
        String finalMessage = message;
        uploadedProductList.forEach(s -> {
            s.setStatus(finalStatus);
            s.setMessage(finalMessage);
            uploadRecordService.update(s, new EntityWrapper<UploadRecordEntity>()
                    .eq("platform_account", s.getPlatformAccount())
                    .eq("original_product_id", s.getOriginalProductId())
                    .eq("upload_id", s.getUploadId()));
        });

    }
}
