package kin.sdk.core;

import java.math.BigDecimal;

public interface Balance {

    BigDecimal value();

    String value(int precision);

}