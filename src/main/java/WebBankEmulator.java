import customer.Customer;
import customer.CustomerService;
import products.CreditCard;
import products.Loan;
import products.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebBankEmulator {
    private static CustomerService customerService = new CustomerService();

    public static void main(String[] args) {

        Long customerId = 1L;
        CompletableFuture<List<Product>> allProducts =
                customerService.getLoansOf(customerId)
                        .thenCombine(
                                customerService.getCreditCardsOf(customerId),
                                (loans, creditCards) -> showProducts(loans, creditCards) );
    }

    private static List<Product> showProducts(List<Loan> loans, List<CreditCard> creditCardsOf) {

        Stream<Product> products = loans.stream().map(loan -> (Product) loan)
                .flatMap(product -> creditCardsOf.stream().map(creditCard -> (Product) creditCard));

        products.forEach( product -> System.out.println(product.getCustomerId()));

        return products.collect(Collectors.toList());
    }


}
