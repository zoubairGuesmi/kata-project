package domain;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import constant.Type;
import exception.AmountToWithdrawHigherThanBalanceException;
import exception.DifferentCurrencyOperationException;
import exception.NegativeDepositAmountException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AccountTest {

    @Test
    public void should_add_deposit_amount_to_balance() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToAdd = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        //WHEN
        Money newBalance = account.deposit(amountToAdd);

        //THEN
        assertEquals(new BigDecimal(1200), newBalance.getAmount());
    }

    @Test
    public void should_throw_exception_when_negative_deposit_amount_added_to_balance() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();
        Money amountToAdd = Money.builder()
                .amount(new BigDecimal(-200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        //WHEN
        NegativeDepositAmountException thrown = Assertions.assertThrows(
                NegativeDepositAmountException.class,
                () -> account.deposit(amountToAdd),
                "Expected deposit method to throw, but it didn't"
        );

        //THEN
        assertEquals("Could not add negative amount to balance", thrown.getMessage());
        assertEquals(new BigDecimal(1000), account.getBalance().getAmount());
    }

    @Test
    public void should_throw_exception_when_account_currency_is_different_from_operation_currency() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();
        Money amountToAdd = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("USD"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        //WHEN
        DifferentCurrencyOperationException thrown = Assertions.assertThrows(
                DifferentCurrencyOperationException.class,
                () -> account.deposit(amountToAdd),
                "Expected deposit method to throw, but it didn't"
        );

        //THEN
        assertEquals("Operation currency should be the same of the account", thrown.getMessage());
        assertEquals(new BigDecimal(1000), account.getBalance().getAmount());
    }

    @Test
    public void should_retrieve_withdrawal_amount_from_balance() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdraw = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        //WHEN
        Money newBalance = account.withdraw(amountToWithdraw);

        //THEN
        assertEquals(new BigDecimal(800), newBalance.getAmount());
    }

    @Test
    public void should_not_retrieve_money_amount_higher_than_balance() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdraw = Money.builder()
                .amount(new BigDecimal(1200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();


        //WHEN
        AmountToWithdrawHigherThanBalanceException thrown = Assertions.assertThrows(
                AmountToWithdrawHigherThanBalanceException.class,
                () -> account.withdraw(amountToWithdraw),
                "Expected withdraw method to throw an exception, but it didn't"
        );

        //THEN
        assertEquals("Amount to withdraw is higher than current balance", thrown.getMessage());
        assertEquals(new BigDecimal(1000), currentBalance.getAmount());
    }

    @Test
    public void should_list_all_operation_of_an_account() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdrawTwoHundred = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdrawFourHundred = Money.builder()
                .amount(new BigDecimal(400))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToDepositFiveHundred = Money.builder()
                .amount(new BigDecimal(500))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToDepositOneHundred = Money.builder()
                .amount(new BigDecimal(100))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        account.withdraw(amountToWithdrawTwoHundred);
        account.withdraw(amountToWithdrawFourHundred);
        account.deposit(amountToDepositFiveHundred);
        account.deposit(amountToDepositOneHundred);

        //WHEN
        List<Operation> operations = account.getOperations();

        //THEN
        assertEquals(4, operations.size());
    }

    @Test
    public void should_create_deposit_operation_when_deposit_money() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToDeposit = Money.builder()
                .amount(new BigDecimal(300))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        //WHEN
        account.deposit(amountToDeposit);

        //THEN
        assertEquals(1, account.getOperations().size());
        Operation operation = account.getOperations().get(0);
        assertEquals(Type.DEPOSIT, operation.getType());
        assertEquals(new BigDecimal(300), operation.getMoney().getAmount());
        assertEquals(new BigDecimal(1300), operation.getBalance().getAmount());
    }

    @Test
    public void should_create_withdraw_operation_when_withdraw_money() {
        //GIVEN
        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdraw = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        //WHEN
        account.withdraw(amountToWithdraw);

        //THEN
        assertEquals(1, account.getOperations().size());
        Operation operation = account.getOperations().get(0);
        assertEquals(Type.WITHDRAW, operation.getType());
        assertEquals(new BigDecimal(200), operation.getMoney().getAmount());
        assertEquals(new BigDecimal(800), operation.getBalance().getAmount());
    }

    @Test
    public void should_display_all_operations() {
        //GIVEN
        Logger accountLogger = (Logger) LoggerFactory.getLogger(Account.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        accountLogger.addAppender(listAppender);


        Money currentBalance = Money.builder()
                .amount(new BigDecimal(1000))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdrawTwoHundred = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToWithdrawFourHundred = Money.builder()
                .amount(new BigDecimal(400))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToDepositFiveHundred = Money.builder()
                .amount(new BigDecimal(500))
                .currency(Currency.getInstance("EUR"))
                .build();

        Money amountToDepositTwoHundred = Money.builder()
                .amount(new BigDecimal(200))
                .currency(Currency.getInstance("EUR"))
                .build();

        Account account = Account.builder()
                .balance(currentBalance)
                .build();

        account.withdraw(amountToWithdrawTwoHundred);
        account.withdraw(amountToWithdrawFourHundred);
        account.deposit(amountToDepositFiveHundred);
        account.deposit(amountToDepositTwoHundred);

        //WHEN
        account.displayOperations();

        //THEN
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("Start displaying operations", logsList.get(0)
                .getMessage());
        assertEquals(Level.INFO, logsList.get(0)
                .getLevel());

        assertTrue(logsList.get(1).getMessage().contains("amount of operation=200 EUR, type of operation=WITHDRAW," +
                " balance after operation=800 EUR]"));
        assertEquals(Level.INFO, logsList.get(1)
                .getLevel());

        assertTrue(logsList.get(2).getMessage().contains("amount of operation=400 EUR, type of operation=WITHDRAW, " +
                "balance after operation=400 EUR]"));
        assertEquals(Level.INFO, logsList.get(2).getLevel());

        assertTrue(logsList.get(3).getMessage().contains("amount of operation=500 EUR, type of operation=DEPOSIT, " +
                "balance after operation=900 EUR]"));
        assertEquals(Level.INFO, logsList.get(3)
                .getLevel());

        assertTrue(logsList.get(4).getMessage().contains("amount of operation=200 EUR, type of operation=DEPOSIT, " +
                "balance after operation=1100 EUR]"));
        assertEquals(Level.INFO, logsList.get(4)
                .getLevel());

        assertEquals("End displaying operations", logsList.get(5)
                .getMessage());
        assertEquals(Level.INFO, logsList.get(5)
                .getLevel());
    }

}
