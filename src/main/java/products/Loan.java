package products;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class Loan extends Product{
    private int duration;
    private long totalAmount;
    private long monthlyAmount;

    private long overdueInterestPayment;

    @Builder
    public Loan(UUID id, Long customerId, int duration, long totalAmount, long monthlyAmount, long overdueInterestPayment) {
        super(id, customerId);
        this.duration = duration;
        this.totalAmount = totalAmount;
        this.monthlyAmount = monthlyAmount;
        this.overdueInterestPayment = overdueInterestPayment;
    }
}
