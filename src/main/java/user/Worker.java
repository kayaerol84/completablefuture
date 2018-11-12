package user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Worker {
    private final Long id;
    private final String corporateId;
    private final String name;
    private final String workAddress;
}
