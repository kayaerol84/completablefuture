package products;

import customer.Customer;
import resource.AbstractService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LoanService extends AbstractService {

    public CompletableFuture<List<Loan>> getLoansOf(Long customerId) {
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

}
