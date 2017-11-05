package kin.sdk.core.mock;


import java.util.Random;

import kin.sdk.core.TransactionId;

public class MockTransactionId implements TransactionId {

    @Override
    public String id() {
        return "transaction#555" + new Random().nextInt();
    }
}