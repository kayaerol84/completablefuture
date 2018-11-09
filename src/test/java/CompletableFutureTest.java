import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void completedFutureExample() throws ExecutionException, InterruptedException {
        CompletableFuture cf = CompletableFuture.completedFuture("message");
        assertTrue(cf.isDone());
        assertEquals("message", cf.get());
        assertEquals("message", cf.getNow(null));
    }

    @Test
    public void runAsyncExample() {
        CompletableFuture cf = CompletableFuture.runAsync(() -> {
            //System.out.println(Thread.currentThread().getName());
            assertTrue(Thread.currentThread().isDaemon());
            sleep(300);
        });
        assertFalse(cf.isDone());
        sleep(301);
        assertTrue(cf.isDone());
    }

    @Test
    public void thenApplyExample() {
        CompletableFuture cf = CompletableFuture.completedFuture("message").thenApply(s -> {
            //System.out.println(Thread.currentThread().getName());
            assertFalse(Thread.currentThread().isDaemon());
            return s.toUpperCase();
        });
        assertEquals("MESSAGE", cf.getNow(null));
    }


    @Test
    public void thenApplyAsyncExample() {
        CompletableFuture cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(s -> {
                    //System.out.println(Thread.currentThread().getName());
                    assertTrue(Thread.currentThread().isDaemon());
                    sleep(200);
                    return s.toUpperCase();
                });
        // TODO
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }


    private static void sleep(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
