import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompletableFutureTest {
    @Test
    public void testSupplier(){

    }

    @Test
    public void testConsumer(){

    }

    @Test
    public void testReduce(){

    }

    @Test
    public void testFlatMap(){

    }

    @Test
    public void testPipeline(){

    }

    @Test
    public void testCommonForkJoinPool(){

    }

    @Test
    public void testSameThreadPool(){
        CompletableFuture<List<User>> cf = CompletableFuture.supplyAsync(
                () -> Arrays.asList(User.builder().id(1L).name("Naive user").build(),
                        User.builder().id(2L).name("Daft user").build(),
                        User.builder().id(3L).name("Weirdo").build())
        );

        ExecutorService executor = Executors.newCachedThreadPool();

        cf.thenApplyAsync(this::readUsers, executor);

        cf.thenRun(() -> System.out.println("Info; " + Thread.currentThread() ));

        cf.thenAcceptAsync(users -> {
            users.forEach(user -> System.out.println(user.getName()));
            System.out.println(" - " + Thread.currentThread());
        }, executor);
    }

    private List<Long> readUsers(List<User> list) {
        System.out.println(" * " + Thread.currentThread());
        return list.stream().map(User::getId).collect(Collectors.toList());
    }

    private static void sleep(int timeout){

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    Function<List<Long>, List<User>> fetchUsers = (ids) -> {
        sleep(300);
        return ids.stream()
                .map(id -> User.builder().id(id).build())
                .collect(Collectors.toList());
    };

    Consumer<List<User>> displayer = users -> users.forEach(System.out::println);
}
