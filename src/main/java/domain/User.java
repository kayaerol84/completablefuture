package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
public class User {
    private Long id;
    private String name;
}