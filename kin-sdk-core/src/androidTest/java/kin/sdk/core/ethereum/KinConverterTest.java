package kin.sdk.core.ethereum;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.ethereum.geth.BigInt;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class KinConverterTest {

    @Test
    @Parameters({"1, 0.000000000000000001",  // 1 Wei to Kin (smallest coin value)
        "10000000000000000000000000000000, 10000000000000.000000000000000000", //Max Kin Supply
        "0.1, 0.000000000000000000"}) //Invalid Wei (wei is integer)
    public void toKinTest(String input, String output) throws Exception {
        BigDecimal bigDecimalWei = new BigDecimal(input);
        BigDecimal bigDecimalKin = KinConverter.toKin(bigDecimalWei);
        assertEquals(output, bigDecimalKin.toPlainString());
    }

    @Test
    @Parameters({"0.123456, 0",  // discarded Fraction
        "987.123456, 987", //discarded fraction with some radix
        "10000000000000000000000000000000.0, 10000000000000000000000000000000"}) //maxWei
    public void toBigIntTest(String input, String output) throws Exception {
        BigDecimal bigDecimal = new BigDecimal(input);
        BigInt bigInt = KinConverter.toBigInt(bigDecimal);
        assertEquals(output, bigInt.string());
    }

    @Test
    @Parameters({"1, 0.000000000000000001",  // one wei (smallest coin value)
        "10000000000000000000000000000000, 10000000000000.000000000000000000", //max wei supply to kin
        "1234567, 0.000000000001234567"})
    public void toKinFromBigIntTest(String input, String output) throws Exception {
        BigInt bigIntWei = KinConverter.toBigInt(new BigDecimal(input));
        BigDecimal bigDecimalKin = KinConverter.toKin(bigIntWei);
        assertEquals(output, bigDecimalKin.toPlainString());
    }

    @Test
    @Parameters({"1, 1000000000000000000",  // 1 kin to wei
        //max kin supply to wei
        "10000000000000.000000000000000000, 10000000000000000000000000000000.000000000000000000",
        "0.000000000001234567, 1234567.000000000000000000"})
    public void fromKinTest(String input, String output) throws Exception {
        BigDecimal bigDecimalKin = new BigDecimal(input);
        BigDecimal bigDecimalWei = KinConverter.fromKin(bigDecimalKin);
        assertEquals(output, bigDecimalWei.toPlainString());
    }

}