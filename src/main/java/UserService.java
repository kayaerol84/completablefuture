import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserService {

    Function<List<Long>, List<User>> fetchUsers = (ids) -> {
        sleep(300);
        return ids.stream()
                .map(id -> User.builder().id(id).build())
                .collect(Collectors.toList());
    };

    Consumer<List<User>> displayer = users -> users.forEach(System.out::println);

    public void sleep(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
