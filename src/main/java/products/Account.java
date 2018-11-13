package products;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Account extends Product {
    private String accountNumber;
    private BigDecimal balance;
    @Builder
    public Account(UUID id, Long customerId, String accountNumber, BigDecimal balance) {
        super(id, customerId);
        this.accountNumber = accountNumber;
        this.balance = balance;
    }
}
