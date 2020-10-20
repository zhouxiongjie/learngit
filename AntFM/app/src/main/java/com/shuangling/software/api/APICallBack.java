package com.shuangling.software.api;


public abstract class APICallBack<T> {
    public abstract void onSuccess(T t);
    public abstract void onFail(String error);
}
