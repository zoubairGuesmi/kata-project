package domain;

import constant.Type;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
public class Operation {
    private Date date;
    private Money money;
    private Type type;
    private Money balance;

    @Override
    public String toString() {
        return "Operation[" +
                "date=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) +
                ", amount of operation=" + money.getAmount() + " " + money.getCurrency() +
                ", type of operation=" + type +
                ", balance after operation=" + balance.getAmount() + " " + balance.getCurrency() +
                ']';
    }
}
