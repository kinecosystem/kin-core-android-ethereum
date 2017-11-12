package kin.sdk.core.ethereum;

import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;

import java.math.BigDecimal;

/**
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
}
