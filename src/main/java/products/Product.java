package products;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class Product {
    public UUID id;
    public Long customerId;
}
