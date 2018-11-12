package customer;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import products.Product;

import java.util.List;

@Builder
@Getter
@ToString
public class Customer {
    private Long id;
    private String name;
    private List<Product> products;
}