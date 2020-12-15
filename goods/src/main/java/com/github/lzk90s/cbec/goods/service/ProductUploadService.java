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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
            entity.setProductId(id);
            entity.setId(uploadId);
            return entity;
        }).collect(Collectors.toList());
        uploadRecordService.insertBatch(uploadRecordList);
    }

    public void checkUploadStatus(PlatformAccountDTO platformAccount) {
        var uploadList = uploadRecordService.selectList(new EntityWrapper<UploadRecordEntity>()
                .eq("platform_account", platformAccount.getPlatformUser())
                .groupBy("id"));
        if (CollectionUtils.isEmpty(uploadList)) {
            return;
        }
        uploadList.forEach(uploadRecord -> {
            var statusDTO = goodsSpiderApiFeign.getUploadStatus(platformAccount.getPlatform(),
                    platformAccount.getApiToken(), uploadRecord.getId());
            uploadRecordService.insertOrUpdate(UploadRecordEntity.getConverter().doBackward(statusDTO));
        });
    }
}
