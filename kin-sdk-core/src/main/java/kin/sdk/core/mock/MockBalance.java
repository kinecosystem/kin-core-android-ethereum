package kin.sdk.core.mock;

import java.math.BigDecimal;

import kin.sdk.core.Balance;

public class MockBalance implements Balance {

    @Override
    public BigDecimal value() {
        return new BigDecimal("1234.56");
    }

    @Override
    public String value(int precision) {
        return "1234.56";
    }

    @Override
    public String toString() {
        return "1234.56";
    }
}