package kin.sdk.core.exception;

/**
 * Created by shaybaz on 03/12/2017.
 */

public class AccountDeletedOpreationFailedException extends OperationFailedException {

    public static final String ACCOUNT_DELETED_ERROR_MESSAGE = "Account deleted, Create new account";
    public AccountDeletedOpreationFailedException() {
        super(ACCOUNT_DELETED_ERROR_MESSAGE);
    }
}
