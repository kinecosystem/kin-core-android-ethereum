package kin.sdk.core.ethereum;

import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Project - Kin SDK
 * Created by Oren Zakay on 07/11/2017.
 *
 * A Utility class used to convert currency values to/from Kin.
 */
class KinConverter {

    private static final BigDecimal KIN = BigDecimal.TEN.pow(18);

    static BigInt fromKin(BigDecimal valueInKin) {
        return Geth.newBigInt(valueInKin.longValue());
    }

    static BigDecimal toKin(BigDecimal value) {
        return value.multiply(KIN);
    }

    static BigDecimal toKin(BigInt value) {
        return new BigDecimal(value.string()).divide(KIN, 18, BigDecimal.ROUND_FLOOR);
    }

    static BigDecimal toKin(BigInteger value) {
        return new BigDecimal(value).divide(KIN, 18, BigDecimal.ROUND_FLOOR);
    }
}
