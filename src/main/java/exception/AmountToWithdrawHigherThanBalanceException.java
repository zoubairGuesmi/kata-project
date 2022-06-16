package exception;

public class AmountToWithdrawHigherThanBalanceException extends RuntimeException {

    public AmountToWithdrawHigherThanBalanceException() {
        super("Amount to withdraw is higher than current balance");
    }
}