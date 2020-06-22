package com.shuangling.software.entity;

import java.io.Serializable;

public class StsInfo implements Serializable {


    private static final long serialVersionUID = -5664005871429702310L;
    /**
     * SecurityToken : CAISwQJ1q6Ft5B2yfSjIr5b/G/3CjpV007qqcVT8gUIMf+5inKLEgDz2IHpLf3dhB+AftvQ/mm1X6/sblqptToQdyYZ2ygY2vPpt6gqET9frRKXXhOV2cfTHdEGXDxnkpsCwB8zyUNLafNq0dlnAjVUd6LDmdDKkLXvHVoOOhcxIY8gvWgCpTDxIA7UqOAxu+uAXKUXQOfuqChrjk0PNEEoKxhR7hTFQ5LiBw8GR7SLahUzbzOESxfzvJZT2QMRwApZzXtCU2uJxDN2jthRd8B9X7qx7obVZ8DPcxLGnDkJW/g+NPpi/kLQtDncgO/JjRPEb/Kigzqwh67GJremtlUYRZ9Myej/EWYWtzPHDHO6ULdsjfd75QS+XjYvRZ8el6lt0MSxGa14XIsBYI3txGAEqVDbWMquu6UrNZgqzsxQKCQHfY/AagAGBRt+7zMDvuNi4LzGHEqPcmqdL91btcrPpSCZ2/uUpmSXKkQivLPcYomIGA39ip3e2q2RwEN+tPgp8lqqMhWv2pmBMpafVbkCVumAS6KL2/8gn4pnpIQWntiDaDJbkAbkx6OsrwAs/yVza/0ZK4I6YyIVHFq0LN9Y0wR2XSFV3NQ==
     * AccessKeyId : STS.NUJPGvcJUdxAsrMeBYsaNsgob
     * AccessKeySecret : FmcAN8xdjw3WT5xUfa3Pn8kHb7LR1MGGhFon5U46qeju
     * Expiration : 2020-05-28T06:27:10Z
     */

    private String SecurityToken;
    private String AccessKeyId;
    private String AccessKeySecret;
    private String Expiration;

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String SecurityToken) {
        this.SecurityToken = SecurityToken;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String AccessKeyId) {
        this.AccessKeyId = AccessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String AccessKeySecret) {
        this.AccessKeySecret = AccessKeySecret;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String Expiration) {
        this.Expiration = Expiration;
    }
}
