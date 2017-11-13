package kin.sdk.core.impl;

import kin.sdk.core.TransactionId;

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
