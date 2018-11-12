package products;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@ToString
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
