package kin.sdk.core.impl;

import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.exception.OperationFailedException;

/**
 * Created by berryventuralev on 02/11/2017.
 */

public abstract class AbstractKinAccount implements KinAccount {


    public abstract Balance getBalanceSync() throws OperationFailedException;
}
