package customer;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import products.Product;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@ToString
public class Customer {
    private UUID id;
    private String name;
    private List<Product> products;
}