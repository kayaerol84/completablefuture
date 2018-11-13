package products;

import customer.CustomerService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AccountService extends CustomerService {

    /**
     * CustomerAccountsAPI
     * @param customerId
     * @return
     */
    public CompletableFuture<List<Account>> getAccountsOf(Long customerId) {
        sleep(600);
        List<Account> creditCards = customers.get(customerId).getProducts()
                .stream()
                .filter(Account.class::isInstance)
                .map(product -> (Account) product)
                .collect(Collectors.toList());

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread in getAccountsOf : " + Thread.currentThread().getName());
            System.out.println("Is Thread daemon : " + Thread.currentThread().isDaemon());
            return creditCards;
        });
    }
}
