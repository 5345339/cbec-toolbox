package com.github.lzk90s.cbec.goods.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.lzk90s.cbec.common.exception.BizException;
import com.github.lzk90s.cbec.goods.dao.entity.ProductEntity;
import com.github.lzk90s.cbec.goods.dao.entity.ProductResourceEntity;
import com.github.lzk90s.cbec.goods.dao.entity.ProductSkuEntity;
import com.github.lzk90s.cbec.goods.dao.entity.UploadRecordEntity;
import com.github.lzk90s.cbec.goods.feign.GoodsSpiderApiFeign;
import com.github.lzk90s.cbec.internal.api.auth.PlatformAccountDTO;
import com.github.lzk90s.cbec.internal.api.spider.ProductDTO;
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
        var productList = productIdList.stream().map(id -> {
            ProductDTO dto = ProductEntity.getConverter().doForward(productService.selectById(id));
            dto.setSkuList(productSkuService.selectList(new EntityWrapper<ProductSkuEntity>()
                    .eq("product_id", id)).stream()
                    .map(sku -> ProductSkuEntity.getConverter().doForward(sku))
                    .collect(Collectors.toList()));
            dto.setImageList(productResourceService.selectList(new EntityWrapper<ProductResourceEntity>()
                    .eq("product_id", id)).stream()
                    .map(img -> ProductResourceEntity.getConverter().doForward(img))
                    .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        int skuNum = productList.stream().mapToInt(s -> s.getSkuList().size()).sum();
        if (skuNum > maxUploadSkuPerTime) {
            throw new BizException("当前上传sku数量" + skuNum + ", 超出单次上传sku数最大限制" + maxUploadSkuPerTime);
        }

        var uploadId = goodsSpiderApiFeign.uploadProduct(platformAccount.getPlatform(),
                platformAccount.getApiToken(), productList);
        if (StringUtils.isEmpty(uploadId)) {
            throw new BizException("上传id为空");
        }

        var uploadRecordList = productIdList.stream().map(id -> {
            var entity = new UploadRecordEntity();
            entity.buildId(platformAccount.getPlatformUser(), uploadId, id);
            entity.setProductId(id);
            entity.setPlatformAccount(platformAccount.getPlatformUser());
            entity.setStatus(UploadStatusEnum.PREPARE.getStatus());
            entity.setUploadId(uploadId);
            return entity;
        }).collect(Collectors.toList());
        uploadRecordService.insertBatch(uploadRecordList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkUploadStatus(PlatformAccountDTO platformAccount) {
        var uploadIdList = uploadRecordService.selectList(new EntityWrapper<UploadRecordEntity>()
                .eq("platform_account", platformAccount.getPlatformUser())
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
            if (null != statusDTO) {
                var entity = new UploadRecordEntity();
                entity.setStatus(statusDTO.getStatus());
                entity.setMessage(statusDTO.getMessage());
                uploadRecordService.update(entity, new EntityWrapper<UploadRecordEntity>()
                        .eq("platform_account", platformAccount.getPlatformUser())
                        .eq("upload_id", r)
                        .in("product_id", statusDTO.getProductList()));
            }
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

        try{
            goodsSpiderApiFeign.enableProductSale(platformAccount.getPlatform(), platformAccount.getApiToken(),
                    uploadedProductList.stream().map(UploadRecordEntity::getProductId).collect(Collectors.toList()));
            status = UploadStatusEnum.IN_SALE.getStatus();
            message = "已上架";
        }catch (Exception e){
            status = null;
            message = e.getMessage();
        }

        String finalStatus = status;
        String finalMessage = message;
        uploadRecordService.updateBatchById(uploadedProductList.stream()
                .peek(s -> s.setStatus(finalStatus))
                .peek(s -> s.setMessage(finalMessage))
                .collect(Collectors.toList()));
    }
}
