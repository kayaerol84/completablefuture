package customer;

import products.Account;
import products.CreditCard;
import products.Loan;
import products.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public abstract class CustomerService {

    protected static final Map<Long, Customer> customers;

    static {
        customers = new HashMap<>();
        customers.put(1L, buildWithCreditCard(1L, "Citizen Kane", "2244"));
        customers.put(2L, buildWithCreditCard(2L, "Vito Carleone", "6666"));
        customers.put(3L, buildWithCreditCard(3L, "Allen Iverson", "1996"));
        customers.put(4L, buildWithLoan(4L, "Bruce Wayne"));
        customers.put(5L, buildWithLoan(5L, "Peter Parker"));
        customers.put(6L, buildWithLoan(6L, "Lord Aragorn"));
        customers.put(7L, buildWithMultipleProducts(7L, "Peter Petrelli", "1234", "NL12345678"));
        customers.put(8L, buildWithMultipleProducts(8L, "Jean Grey", "8888", "NL456789012"));
        customers.put(9L, buildWithMultipleProducts(9L, "Stan Lee", "9999", "BE12345678"));
        customers.put(10L, buildWithMultipleProducts(10L, "Yuri Gagarin", "1961", "BE34567812"));
    }

    private static Customer buildWithMultipleProducts(Long customerId, String name, String lastDigits, String accountNumber) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .name(name)
                .products(Arrays.asList(buildLoan(customerId), buildCreditCard(customerId, lastDigits),
                        buildAccount(customerId, accountNumber)))
                .build();
    }

    private static Product buildAccount(Long customerId, String accountNumber) {
        return Account.builder()
                .customerId(customerId)
                .accountNumber(accountNumber)
                .balance(new BigDecimal(10000))
                .build();
    }

    private static Customer buildWithCreditCard(Long customerId, String name, String lastDigits) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .name(name)
                .products(Collections.singletonList(buildCreditCard(customerId, lastDigits)))
                .build();
    }

    private static Customer buildWithLoan(Long customerId, String name){
        return Customer.builder()
                .id(UUID.randomUUID())
                .name(name)
                .products(Collections.singletonList(buildLoan(customerId)))
                .build();
    }
    private static Product buildCreditCard(Long customerId, String lastDigits) {
        return CreditCard.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .expireDate(LocalDate.of(2022, 2, 2))
                .cardNumber("4488****----****"+lastDigits)
                .build();
    }
    private static Product buildLoan(Long customerId) {
        return Loan.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .duration(24)
                .monthlyAmount(1000)
                .totalAmount(24000)
                .build();
    }

    public static Product buildLoanWithOverdue(Loan loan, long overdue){
        sleep(200);
        loan.setOverdueInterestPayment(overdue);
        return loan;
    }

    public static void sleep(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
