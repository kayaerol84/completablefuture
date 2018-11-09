import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserHandler {

    private final UserService userService;

    public static void main(String[] args) {
        new UserHandler(new UserService()).doSomething();
    }

    private void doSomething(){
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Supplier<List<Long>> ids = () -> {
            userService.sleep(200);
            return Arrays.asList(1L,2L,3L);
        };
        final CompletableFuture<List<Long>> completableFuture = CompletableFuture.supplyAsync(ids);
        completableFuture.thenApplyAsync(userService.fetchUsers, executor)
                .thenAcceptAsync(userService.displayer, executor);

        userService.sleep(1000);

        executor.shutdown();
    }
}
