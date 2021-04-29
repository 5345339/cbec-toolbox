package com.cbec.internal.api.ecommerce_facade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 翻译api
 * @author : qihang.liu
 * @date 2021-04-29
 */
public interface TranslateApi {
    @GetMapping("/translate")
    String translate(@RequestParam("from") String from, @RequestParam("top") String to, @RequestParam("words") String words);
}
