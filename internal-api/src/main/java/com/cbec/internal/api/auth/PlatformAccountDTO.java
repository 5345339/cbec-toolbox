package com.cbec.internal.api.auth;

import lombok.Data;

@Data
public class PlatformAccountDTO {
    private String user;
    private String platform;
    private String platformUser;
    private String platformPassword;
    private String apiToken;
}
