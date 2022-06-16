package exception;

public class DifferentCurrencyOperationException extends RuntimeException {
    public DifferentCurrencyOperationException() {
        super("Operation currency should be the same of the account");
    }

}
