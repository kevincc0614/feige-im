package com.ds.feige.im.account.configurer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author DC
 */
@ConfigurationProperties("account.security")
public class AccountSecurityProperties {
    private String tokenSignSecret;

    public String getTokenSignSecret() {
        return tokenSignSecret;
    }

    public void setTokenSignSecret(String tokenSignSecret) {
        this.tokenSignSecret = tokenSignSecret;
    }
}
