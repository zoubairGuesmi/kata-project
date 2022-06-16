package exception;

public class NegativeDepositAmountException extends RuntimeException {
    public NegativeDepositAmountException() {
        super("Could not add negative amount to balance");
    }
}
