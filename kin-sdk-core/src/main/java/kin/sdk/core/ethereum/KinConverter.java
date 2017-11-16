package kin.sdk.core.ethereum;

import java.math.BigDecimal;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;

/**
 * A Utility class used to convert currency values to/from Kin.
 */
class KinConverter {

    private static final BigDecimal KIN = BigDecimal.TEN.pow(18);

    /**
     * Convert the radix part (integer) of input BigDecimal to Geth BigInt
     * NOTE: any fractional part will be discarded!
     *
     * @param value expected
     * @return Integer part of input BigDecimal as BigInt
     */
    static BigInt toBigInt(BigDecimal value) {
        BigInt bigInt = Geth.newBigInt(0L);
        //to get ByteArray representation, convert to Java BigInteger (will discard the fractional part)
        //then extract ByteArray from the BigInteger, BigInteger representation is in two's complement,
        // but as we're not dealing with negative numbers, it's safe to init the Unsigned BigInt with it
        bigInt.setBytes(value.toBigInteger().toByteArray());
        return bigInt;
    }

    static BigDecimal fromKin(BigDecimal value) {
        return value.multiply(KIN);
    }

    static BigDecimal toKin(BigInt value) {
        return toKin(new BigDecimal(value.string()));
    }

    static BigDecimal toKin(BigDecimal value) {
        return value.divide(KIN, 18, BigDecimal.ROUND_FLOOR);
    }
}
