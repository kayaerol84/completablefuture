package products;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Product {
    public Long id;
    public Long customerId;
}
