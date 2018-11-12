package resource;

import customer.Customer;
import products.CreditCard;
import products.Loan;
import products.Product;

import java.time.LocalDate;
import java.util.*;

public abstract class AbstractService {

    public static final Map<Long, Customer> customers;

    static {
        customers = new HashMap<>();
        customers.put(1L, buildWithCreditCard(1L, "Citizen Kane", "2244"));
        customers.put(2L, buildWithCreditCard(2L, "Vito Carleone", "6666"));
        customers.put(3L, buildWithCreditCard(3L, "Allen Iverson", "1996"));
        customers.put(4L, buildWithLoan(4L, "Bruce Wayne"));
        customers.put(5L, buildWithLoan(5L, "Peter Parker"));
        customers.put(6L, buildWithLoan(6L, "Lord Aragorn"));
        customers.put(7L, buildWithMultipleProducts(7L, "Peter Petrelli", "1234"));
        customers.put(8L, buildWithMultipleProducts(8L, "Jean Grey", "8888"));
    }

    public static Customer buildWithMultipleProducts(Long customerId, String name, String lastDigits) {
        return Customer.builder()
                .id(customerId)
                .name(name)
                .products(Arrays.asList(buildLoan(customerId), mockCreditCard(customerId, lastDigits)))
                .build();
    }

    public static Customer buildWithCreditCard(Long customerId, String name, String lastDigits) {
        return Customer.builder()
                .id(customerId)
                .name(name)
                .products(Collections.singletonList(mockCreditCard(customerId, lastDigits)))
                .build();
    }

    public static Customer buildWithLoan(Long customerId, String name){
        return Customer.builder()
                .id(customerId)
                .name(name)
                .products(Collections.singletonList(buildLoan(customerId)))
                .build();
    }
    public static Product mockCreditCard(Long customerId, String lastDigits) {
        return CreditCard.builder()
                .id(new Random().nextLong())
                .customerId(customerId)
                .expireDate(LocalDate.of(2022, 2, 2))
                .cardNumber("4488****----****"+lastDigits)
                .build();
    }
    public static Product buildLoan(Long customerId) {
        return Loan.builder()
                .id(new Random().nextLong())
                .customerId(customerId)
                .duration(24)
                .monthlyAmount(1000)
                .totalAmount(24000)
                .build();
    }

    public static Product buildLoanWithOverdue(Loan loan, long overdue){
        loan.setOverdueInterestPayment(overdue);
        return loan;
    }
}
