package products;

import customer.CustomerService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LoanService extends CustomerService {

    public CompletableFuture<List<Loan>> getLoansOf(Long customerId) {
        sleep(300);
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
