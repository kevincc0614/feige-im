package com.ds.feige.im.oss.configurer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS配置
 *
 * @author DC
 */
@ConfigurationProperties("oss")
public class OSSConfigProperties {
    private String domain;
    private String group;
    private String tokenSecret;
    private Integer tokenExpireSeconds;
    private Long maxFileSize;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public Integer getTokenExpireSeconds() {
        return tokenExpireSeconds;
    }

    public void setTokenExpireSeconds(Integer tokenExpireSeconds) {
        this.tokenExpireSeconds = tokenExpireSeconds;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
