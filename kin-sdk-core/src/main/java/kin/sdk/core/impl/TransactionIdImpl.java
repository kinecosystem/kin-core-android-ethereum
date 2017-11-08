package kin.sdk.core.impl;

import kin.sdk.core.TransactionId;

/**
 * Project - Kin SDK
 * Created by Oren Zakay on 06/11/2017.
 */
public class TransactionIdImpl implements TransactionId {

    private String id;

    public TransactionIdImpl(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }
}
