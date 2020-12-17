package com.cbec.internal.api.spider;

import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface GoodsSpiderApi {
    @GetMapping("/list_all_category/{platform}")
    List<CategoryDTO> listAllCategory(@PathVariable("platform") String platform);

    @GetMapping("/list_category_goods/{platform}")
    ScrollResult<GoodsInfoDTO> listCategoryGoods(@PathVariable("platform") String platform,
                                                 @RequestParam("category") String category,
                                                 @RequestParam("sort") String sort,
                                                 @RequestParam("cursor") String cursor);

    @GetMapping("/search_goods_by_image")
    List<GoodsInfoDTO> searchGoodsByImage(@RequestParam("image_url") String imageUrl,
                                          @RequestParam("max_price") Float maxPrice,
                                          @RequestParam("num") Integer num);

    @GetMapping("/sync_product/{platform}")
    List<ProductDTO> syncProduct(@PathVariable("platform") String platform,
                                 @RequestParam("apiToken") String apiToken,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime);

    @PostMapping("/upload_product/{platform}")
    String uploadProduct(@PathVariable("platform") String platform,
                         @RequestParam("apiToken") String apiToken,
                         @RequestBody List<ProductDTO> productDTOList);

    @GetMapping("/get_upload_status/{platform}")
    UploadStatusDTO getUploadStatus(@PathVariable("platform") String platform,
                                    @RequestParam("apiToken") String apiToken,
                                    @RequestParam("uploadId") String uploadId);

    @PostMapping("/enable_product_sale/{platform}")
    void enableProductSale(@PathVariable("platform") String platform,
                           @RequestParam("apiToken") String apiToken,
                           @RequestBody List<String> productIdList);

    @PostMapping("/delete_product/{platform}")
    void deleteProduct(@PathVariable("platform") String platform,
                           @RequestParam("apiToken") String apiToken,
                           @RequestBody List<String> productIdList);
}
