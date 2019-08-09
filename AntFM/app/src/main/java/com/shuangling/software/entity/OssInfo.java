package com.shuangling.software.entity;


import java.io.Serializable;

public class OssInfo implements Serializable {


    /**
     * access_key_id : STS.NHQ4LdAHRddwys7Ji3gYJw5wp
     * access_key_secret : EL56xoBv2ZP9aN8Wi4fiuQLBZ6gAhh3Z61XPZfXjhcmx
     * expiration : 2019-07-15T07:37:58Z
     * security_token : CAIShQJ1q6Ft5B2yfSjIr4vkf/bQrJdz06ace1WGrmlma9ZmmPDckjz2IHtMe3lqA+Acs/g/nWlZ5/salqJ4T55IQ1Dza8J148znKa9ttM+T1fau5Jko1beHewHKeTOZsebWZ+LmNqC/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/UFB5ZtKWveVzddA8pMLQZPsdITMWCrVcygKRn3mGHdfiEK00he8Tois/3gnZLMukaF3QSrl7Uvyt6vcsT+Xa5FJ4xiVtq55utye5fa3TRYgxowr/wu0/weo2ed443NWgAAskjeKZnd9tx+MQl+fbMmHK1Jqvfxk/Bis/DUjZ7wzxtdxV3aioQ29uoagAFJ3j4aEPbUdGPb977WnU133uRsOAODesMxc81TtoXtamsFF+FGnJIaHfzzEwWPYNRDWKqpm9/EfcgPnsKxjxqt8GjIy82H8z4MeUyVPoQUA136aeAnx/I4vk87UCgDiFbjlH8vJHIeqdp79U9xUmTra0wydbL04iicu1ONPTYQog==
     * host : https://sl-cdn.slradio.cn
     * bucket : sl-cdn
     * dir : cms/user/
     */

    private String access_key_id;
    private String access_key_secret;
    private String expiration;
    private String security_token;
    private String host;
    private String bucket;
    private String dir;

    public String getAccess_key_id() {
        return access_key_id;
    }

    public void setAccess_key_id(String access_key_id) {
        this.access_key_id = access_key_id;
    }

    public String getAccess_key_secret() {
        return access_key_secret;
    }

    public void setAccess_key_secret(String access_key_secret) {
        this.access_key_secret = access_key_secret;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getSecurity_token() {
        return security_token;
    }

    public void setSecurity_token(String security_token) {
        this.security_token = security_token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}