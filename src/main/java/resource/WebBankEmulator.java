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

    public static void main(String[] args) {

        final ExecutorService executor =
                Executors.newFixedThreadPool(10, r -> new Thread(r, "Our cool thread"));

        Long customerId = 5L;

        CompletableFuture<List<Product>> allProducts = loanService.getLoansOf(customerId)
                //.thenCombine(creditCardService.getCreditCardsOf(customerId), WebBankEmulator::showProducts);
                .thenCombineAsync(creditCardService.getCreditCardsOf(customerId), WebBankEmulator::showProducts, executor);

        CompletableFuture<List<Product>> loansWithOverdue = allProducts
                .thenApply( products -> updateOverdueInterest(products));
                //.thenApplyAsync(products -> updateOverdueInterest(products), executor);

        CustomerService.sleep(300);
        loansWithOverdue
                .thenAccept( loans -> outputLoans(loans));
                //.thenAcceptAsync( loans -> outputLoans(loans), executor );
        executor.shutdown();
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

    private static List<Product> showProducts(List<Loan> loans, List<CreditCard> creditCards) {
        System.out.println("****** Products ******");
        System.out.println("Thread in showProducts : " + Thread.currentThread().getName());
        loans.forEach(System.out::println);
        creditCards.forEach(System.out::println);

        List<Product> products = new ArrayList<>();
        products.addAll(loans);
        products.addAll(creditCards);
        return  products;
    }
}
