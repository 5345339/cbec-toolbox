package com.github.lzk90s.cbec.internal.api.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/internal/platform_account")
public interface PlatformAccountApi {
    @GetMapping("/list_platform_account_by_user")
    List<PlatformAccountDTO> listPlatformAccountByUser(@RequestParam("userName") String userName);

    @GetMapping("/list_all_user_platform_account")
    List<PlatformAccountDTO> listAllUserPlatformAccount();

    @GetMapping("/get_user_platform_account")
    PlatformAccountDTO getUserPlatformAccount(@RequestParam("userName") String userName,
                                              @RequestParam("platformAccount") String platformAccount);
}
