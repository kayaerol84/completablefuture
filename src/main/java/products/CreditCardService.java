package products;

import customer.CustomerService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CreditCardService extends CustomerService {

    /**
     * CustomerCreditCardsAPI
     * @param customerId
     * @return
     */
    public CompletableFuture<List<CreditCard>> getCreditCardsOf(Long customerId) {
        sleep(300);
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
}
