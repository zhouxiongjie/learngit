package com.shuangling.software.oss;

import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

public class OSSAKSKCredentialProvider implements OSSCredentialProvider {

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String expiration;





    /**
     * 用阿里云提供的AccessKeyId， AccessKeySecret构造一个凭证提供器
     *
     * @param accessKeyId
     * @param accessKeySecret
     */
    public OSSAKSKCredentialProvider(String accessKeyId, String accessKeySecret,String securityToken,String expiration) {
        setAccessKeyId(accessKeyId.trim());
        setAccessKeySecret(accessKeySecret.trim());
        setSecurityToken(securityToken);
        setExpiration(expiration);
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Override
    public OSSFederationToken getFederationToken() {
        return new OSSFederationToken(accessKeyId, accessKeySecret, securityToken, expiration);
    }
}
