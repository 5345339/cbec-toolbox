package com.cbec.goods.service;

import com.cbec.common.exception.BizException;
import com.cbec.goods.dao.entity.ProductEntity;
import com.cbec.goods.dao.entity.ProductResourceEntity;
import com.cbec.goods.dao.entity.ProductSkuEntity;
import com.cbec.internal.api.auth.PlatformAccountDTO;
import com.cbec.internal.api.spider.GoodsSpiderApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductCollectService {
    @Autowired
    private GoodsSpiderApi goodsSpiderApi;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private ProductResourceService productResourceService;

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void asyncCollect(PlatformAccountDTO platformAccount, String startTime, String endTime) {
        if (StringUtils.isEmpty(platformAccount.getApiToken())) {
            throw new BizException("Platform account api token is empty for " + platformAccount.getPlatformUser());
        }

        var productDTOList = goodsSpiderApi.syncProduct(platformAccount.getPlatform(),
                platformAccount.getApiToken(), startTime, endTime);
        if (CollectionUtils.isEmpty(productDTOList)) {
            log.info("No product found for {} between {} to {}", platformAccount, startTime, endTime);
            return;
        }

        log.info("Found {} product for {} between {} to {}", productDTOList.size(), platformAccount,
                startTime, endTime);

        productDTOList.forEach(productDTO -> {
            var productEntity = ProductEntity.getConverter().doBackward(productDTO);
            productEntity.setPlatformAccount(platformAccount.getPlatformUser());
            productService.insertOrUpdate(productEntity);

            var skuEntityList = productDTO.getSkuList().stream()
                    .map(sku -> ProductSkuEntity.getConverter().doBackward(sku))
                    .peek(sku -> sku.setProductId(productDTO.getId()))
                    .collect(Collectors.toList());
            productSkuService.insertOrUpdateBatch(skuEntityList);

            var imageEntityList = productDTO.getImageList().stream()
                    .map(img -> ProductResourceEntity.getConverter().doBackward(img))
                    .peek(img -> img.setProductId(productDTO.getId()))
                    .collect(Collectors.toList());
            productResourceService.insertOrUpdateBatch(imageEntityList);
        });
    }
}
