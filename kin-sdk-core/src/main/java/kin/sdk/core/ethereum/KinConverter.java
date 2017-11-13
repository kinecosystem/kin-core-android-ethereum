package kin.sdk.core.ethereum;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;

/**
 * A Utility class used to convert currency values to/from Kin.
 */
class KinConverter {

    private static final BigDecimal KIN = BigDecimal.TEN.pow(18);

    static BigInt toBigInt(BigDecimal valueInKin) {
        return Geth.newBigInt(valueInKin.longValue());
    }

    static BigDecimal fromKin(BigDecimal value) {
        return value.multiply(KIN);
    }

    static BigDecimal toKin(BigInt value) {
        return new BigDecimal(value.string()).divide(KIN, 18, BigDecimal.ROUND_FLOOR);
    }

    static BigDecimal toKin(BigInteger value) {
        return new BigDecimal(value).divide(KIN, 18, BigDecimal.ROUND_FLOOR);
    }
}
