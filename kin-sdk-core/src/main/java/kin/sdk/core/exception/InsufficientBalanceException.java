package kin.sdk.core.exception;

public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException() {
        super("Not enough Kin");
    }
}
