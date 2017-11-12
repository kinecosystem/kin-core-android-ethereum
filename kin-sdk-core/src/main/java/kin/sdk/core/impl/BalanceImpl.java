package kin.sdk.core.impl;

import java.math.BigDecimal;

import kin.sdk.core.Balance;

public class BalanceImpl implements Balance {

    private BigDecimal valueInKin;

    public BalanceImpl(BigDecimal valueInKin) {
        this.valueInKin = valueInKin;
    }

    @Override
    public BigDecimal value() {
        return valueInKin;
    }

    @Override
    public String value(int precision) {
        return valueInKin.setScale(precision, BigDecimal.ROUND_FLOOR).toString();
    }
}
