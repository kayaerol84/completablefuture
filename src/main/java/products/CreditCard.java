package products;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;


@Getter
public class CreditCard extends Product {
    private String cardNumber;
    private LocalDate expireDate;

    @Builder
    public CreditCard(Long id, Long customerId, String cardNumber, LocalDate expireDate) {
        super(id, customerId);
        this.cardNumber = cardNumber;
        this.expireDate = expireDate;
    }
}
