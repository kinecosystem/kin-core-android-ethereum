package kin.sdk.core;

public interface ResultCallback<T> {

    void onResult(T result);

    void onError(Exception e);
}