package resource;

import customer.CustomerService;
import products.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WebBankEmulator {
    private static LoanService loanService = new LoanService();
    private static CreditCardService creditCardService = new CreditCardService();
    private static AccountService accountService = new AccountService();

    public static void main(String[] args) {

        final ExecutorService executor =
                Executors.newFixedThreadPool(10, r -> new Thread(r, "Our cool thread"));

        Long customerId = 7L;

        CompletableFuture<List<Product>> allProducts = loanService.getLoansOf(customerId)
                .thenCompose(loans -> getProducts(loans, accountService.getAccountsOf(customerId)))
                //.thenComposeAsync(loans -> getProducts(loans, accountService.getAccountsOf(customerId)))
                //.thenCombine(creditCardService.getCreditCardsOf(customerId), WebBankEmulator::showProducts);
                .thenCombineAsync(creditCardService.getCreditCardsOf(customerId), (loans, creditCards) -> showProducts(loans, creditCards), executor);

        CompletableFuture<List<Product>> loansWithOverdue = allProducts
                .thenApply( products -> updateOverdueInterest(products));
                //.thenApplyAsync(products -> updateOverdueInterest(products), executor);

        CustomerService.sleep(300);
        CompletableFuture<Void> result = loansWithOverdue
                .thenAccept( loans -> outputLoans(loans));
                //.thenAcceptAsync( loans -> outputLoans(loans), executor );

        //TODO Can you chain a CF of Void ?
        result.thenRun( () -> System.out.println("This completable future never dies"));

        // TODO anyOf & allOf
        /*Long otherCustomerId = 8L;
        CompletableFuture<Void> allProductsAgain =
                CompletableFuture.allOf(loanService.getLoansOf(otherCustomerId), creditCardService.getCreditCardsOf(otherCustomerId));
        CompletableFuture<Object> anyProductsAgain = CompletableFuture.anyOf(loanService.getLoansOf(otherCustomerId), creditCardService.getCreditCardsOf(otherCustomerId));
        */
        executor.shutdown();
    }

    private static CompletableFuture<List<Product>> getProducts(List<Loan> loans, CompletableFuture<List<Account>> accounts) {
        List<Product> loansAndAccounts = new ArrayList<>();
        return accounts.thenApply( acc -> {
            loansAndAccounts.addAll(acc);
            loansAndAccounts.addAll(loans);
            return loansAndAccounts;
        });

    }

    private static void outputLoans(List<Product> loans) {
        CustomerService.sleep(300);
        System.out.println("Thread in outputLoans : " + Thread.currentThread().getName());
        System.out.println("Is Thread daemon : " + Thread.currentThread().isDaemon());

        loans.forEach( l -> System.out.println(l) );
    }

    private static List<Product> updateOverdueInterest(List<Product> products) {

        System.out.println("Thread in updateOverdueInterest : " + Thread.currentThread().getName());
        System.out.println("Is Thread daemon : " + Thread.currentThread().isDaemon());

        return products.stream().filter(Loan.class::isInstance).map(product -> (Loan) product)
                .map(loan -> CustomerService.buildLoanWithOverdue(loan,10))
                .collect(Collectors.toList());
    }

    private static List<Product> showProducts(List<Product> loansAndAccounts, List<CreditCard> creditCards) {
        System.out.println("****** Products ******");
        System.out.println("Thread in showProducts : " + Thread.currentThread().getName());
        loansAndAccounts.forEach(System.out::println);
        creditCards.forEach(System.out::println);

        System.out.println("****** Products end ******");

        List<Product> products = new ArrayList<>();
        products.addAll(loansAndAccounts);
        products.addAll(creditCards);
        return  products;
    }
}
