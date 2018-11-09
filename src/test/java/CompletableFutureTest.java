import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CompletableFutureTest {

    private static void sleep(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testSupplier(){

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

    static ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
        int count = 1;
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "custom-executor-" + count++);
        }
    });
    @Test
    public void thenApplyAsyncWithExecutorExample() {
        CompletableFuture cf = CompletableFuture.completedFuture("message-x").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().getName().startsWith("custom-executor-"));
            assertFalse(Thread.currentThread().isDaemon());
            sleep(400);
            return s.toUpperCase();
        }, executor);
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE-X", cf.join());
    }

    /**
     * ********************* Consumer
     */
    @Test
    public void thenAcceptExample() {
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture("thenAccept message")
                .thenAccept(s -> {
                    System.out.println("Thread - " + Thread.currentThread().getName());
                    System.out.println("Is Thread deamon? " + Thread.currentThread().isDaemon());
                    result.append(s);
                });
        assertEquals("thenAccept message", result.toString());
    }

    @Test
    public void thenAcceptAsyncExample() {
        StringBuilder result = new StringBuilder();
        CompletableFuture cf = CompletableFuture.completedFuture("thenAcceptAsync message")
                .thenAcceptAsync(s -> {
                    System.out.println("Thread - " + Thread.currentThread().getName());
                    System.out.println("Is Thread deamon? " + Thread.currentThread().isDaemon());
                    result.append(s);
                });
        // TODO thenAcceptAsync(Consumer, executor)
        cf.join();
        assertEquals("thenAcceptAsync message", result.toString());
    }

    /**
     * Completing a Computation exceptionally
     */
    @Test
    public void completeExceptionallyExample() {
        CompletableFuture cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(String::toUpperCase, executor);

        CompletableFuture exceptionHandler = cf.handle((s, th) -> (th != null) ? "message upon cancel" : "");

        cf.completeExceptionally(new RuntimeException("completed exceptionally"));
        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
        try {
            // The join() method doesn't throw checked exceptions.
            // Instead it throws unchecked CompletionException.
            // So you do not need a try-catch block and instead you can fully harness exceptionally() method
            cf.join();
            fail("Should have thrown an exception");
        } catch(CompletionException ex) { // just for testing
            assertEquals("completed exceptionally", ex.getCause().getMessage());
        }
        assertEquals("message upon cancel", exceptionHandler.join());
    }

    /**
     * Cancelling a computation
     */
    @Test
    public void cancelExample() {
        CompletableFuture cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(String::toUpperCase, executor);
        CompletableFuture cf2 = cf.exceptionally(throwable -> "canceled message");
        assertTrue("Was not canceled", cf.cancel(true));
        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
        assertEquals("canceled message", cf2.join());
    }

}
