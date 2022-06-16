package domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Builder
public class Money {

    private BigDecimal amount;
    private Currency currency;

    public Money addAmount(Money amountToAdd) {
        this.amount = amount.add(amountToAdd.getAmount());
        return this;
    }

    public Money retrieveAmount(Money amountToWithdraw) {
        this.amount = amount.subtract(amountToWithdraw.getAmount());
        return this;
    }
}
