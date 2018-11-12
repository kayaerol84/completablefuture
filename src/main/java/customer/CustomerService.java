package customer;

import products.CreditCard;
import products.Loan;
import products.Product;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CustomerService {
    private static final Map<Long, Customer> customers;

    static {
        customers = new HashMap<Long, Customer>();
        customers.put(1L, buildWithCreditCard(1L, "Citizen Kane", "2244"));
        customers.put(2L, buildWithCreditCard(2L, "Vito Carleone", "6666"));
        customers.put(3L, buildWithCreditCard(3L, "Allen Iverson", "1996"));
        customers.put(4L, buildWithLoan(4L, "Bruce Wayne"));
        customers.put(5L, buildWithLoan(5L, "Peter Parker"));
        customers.put(6L, buildWithLoan(6L, "Lord Aragorn"));
        customers.put(7L, buildWithMultipleProducts(7L, "Peter Petrelli", "1234"));
        customers.put(8L, buildWithMultipleProducts(8L, "Jean Grey", "8888"));
    }
    public CompletableFuture<List<Loan>> getLoansOf(Long customerId) {
        Customer customer = customers.get(customerId);
        List<Loan> loans = customers.get(customerId).getProducts().stream()
                .filter(Loan.class::isInstance)
                .map(product -> (Loan) product)
                .collect(Collectors.toList());
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread in getLoansOf : " + Thread.currentThread().getName());
            System.out.println("Is Thread daemon : " + Thread.currentThread().isDaemon());
            return loans;
        });
    }
    public CompletableFuture<List<CreditCard>> getCreditCardsOf(Long customerId) {
        List<CreditCard> creditCards = customers.get(customerId).getProducts().stream()
                .filter(CreditCard.class::isInstance)
                .map(product -> (CreditCard) product)
                .collect(Collectors.toList());
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread in getCreditCardsOf : " + Thread.currentThread().getName());
            System.out.println("Is Thread daemon : " + Thread.currentThread().isDaemon());
            return creditCards;
        });
    }

    private static Customer buildWithMultipleProducts(Long customerId, String name, String lastDigits) {
        return Customer.builder()
                .id(customerId)
                .name(name)
                .products(Arrays.asList(mockLoan(customerId), mockCreditCard(customerId, lastDigits)))
                .build();
    }

    private static Customer buildWithCreditCard(Long customerId, String name, String lastDigits) {
        return Customer.builder()
                .id(customerId)
                .name(name)
                .products(Collections.singletonList(mockCreditCard(customerId, lastDigits)))
                .build();
    }

    private static Customer buildWithLoan(Long customerId, String name){
        return Customer.builder()
                .id(customerId)
                .name(name)
                .products(Collections.singletonList(mockLoan(customerId)))
                .build();
    }
    private static Product mockCreditCard(Long customerId, String lastDigits) {
        return CreditCard.builder()
                .id(new Random().nextLong())
                .customerId(customerId)
                .expireDate(LocalDate.of(2022, 2, 2))
                .cardNumber("4488****----****"+lastDigits)
                .build();
    }
    private static Product mockLoan(Long customerId) {
        return Loan.builder()
                .id(new Random().nextLong())
                .customerId(customerId)
                .duration(24)
                .monthlyAmount(1000)
                .totalAmount(24000)
                .build();
    }
}
