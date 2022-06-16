package domain;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import constant.Type;
import exception.AmountToWithdrawHigherThanBalanceException;
import exception.DifferentCurrencyOperationException;
import exception.NegativeDepositAmountException;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class Account {

    static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Account.class);

    private Money balance;
    @Builder.Default
    private List<Operation> operations = new ArrayList<>();

    public Money deposit(Money amountToAdd) {
        if (amountToAdd.getAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeDepositAmountException();
        if (!amountToAdd.getCurrency().equals(this.getBalance().getCurrency()))
            throw new DifferentCurrencyOperationException();

        this.balance = balance.addAmount(amountToAdd);

        operations.add(Operation.builder()
                .date(new Date())
                .money(amountToAdd)
                .type(Type.DEPOSIT)
                .balance(Money.builder()
                        .amount(this.balance.getAmount())
                        .currency(this.balance.getCurrency())
                        .build())
                .build());

        return balance;
    }

    public Money withdraw(Money amountToWithdraw) {
        if (balance.getAmount().compareTo(amountToWithdraw.getAmount()) < 0)
            throw new AmountToWithdrawHigherThanBalanceException();

        this.balance = balance.retrieveAmount(amountToWithdraw);

        operations.add(Operation.builder()
                .date(new Date())
                .money(amountToWithdraw)
                .type(Type.WITHDRAW)
                .balance(Money.builder()
                        .amount(this.balance.getAmount())
                        .currency(this.balance.getCurrency())
                        .build())
                .build());

        return balance;
    }

    public void displayOperations() {
        LOGGER.info("Start displaying operations");
        operations.forEach(op -> LOGGER.info(op.toString()));
        LOGGER.info("End displaying operations");
    }
}