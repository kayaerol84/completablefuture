import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CompletableFutureExample {

    private static void sleep(int timeout){

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    static Function<List<Long>, List<User>> fetchUsers = (ids) -> {
        sleep(300);
        System.out.println("fetchUsers running in thread; " + Thread.currentThread().getName());
        return ids.stream()
                .map(id -> User.builder().id(id).build())
                .collect(Collectors.toList());
    };

    static Consumer<List<User>> displayer = users -> {
        System.out.println("Running in thread; " + Thread.currentThread().getName());
        users.forEach(System.out::println);
    };

    public static void main(String[] args) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Supplier<List<Long>> ids = () -> {
            sleep(200);
            return Arrays.asList(1L,2L,3L);
        };
        CompletableFuture<List<Long>> completableFuture = CompletableFuture.supplyAsync(ids);
        completableFuture.thenApplyAsync(fetchUsers, executor)
                .thenAcceptAsync(displayer, executor);

        sleep(1000);

        executor.shutdown();
    }
}
