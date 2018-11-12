import user.User;
import user.Worker;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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

    private CompletableFuture<List<User>> users;
    private CompletableFuture<List<Worker>> workers;

    @Before
    public void setup(){
        users = CompletableFuture.supplyAsync(
                () -> Arrays.asList(User.builder().id(1L).name("Naive user").build(),
                        User.builder().id(2L).name("Daft user").build(),
                        User.builder().id(3L).name("Weirdo").build())
        );

        workers = CompletableFuture.supplyAsync(
                () -> Arrays.asList(Worker.builder().id(1L).name("Crazy worker").build(),
                        Worker.builder().id(2L).name("Best worker").build(),
                        Worker.builder().id(2L).name("Best worker").build(),
                        Worker.builder().id(3L).name("Mad worker").build())
        );
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
            System.out.println("runAsyncExample Thread name: " + Thread.currentThread().getName());
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
            System.out.println("thenApplyExample Thread name: " + Thread.currentThread().getName());
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
            // TODO The join() method doesn't throw checked exceptions.
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

    /**
     * thenCombine (reduce), thenCompose (flatMap)
     */
    @Test
    public void thenCombineExample() {
        String original = "Message";
        CompletableFuture cf = CompletableFuture.completedFuture(original).thenApply(s -> delayedUpperCase(s))
                .thenCombine(CompletableFuture.completedFuture(original).thenApply(s -> delayedLowerCase(s)),
                        (s1, s2) -> concat(s1, s2));
        assertEquals("MESSAGEmessage", cf.getNow(null));
    }

    private String concat(String s1, String s2) {
        return s1 + s2;
    }

    @Test
    public void thenCombineAsyncExample() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture.completedFuture(original)
                .thenApplyAsync(s -> delayedUpperCase(s));

        CompletableFuture<String> cf2 = CompletableFuture.completedFuture(original)
                .thenApplyAsync(s -> delayedLowerCase(s));

        CompletableFuture<String> result = cf.thenCombine(cf2, (s1, s2) -> concat(s1, s2));

        assertEquals("MESSAGEmessage", result.join());
    }
/*
    @Test
    public void thenCombineAsyncExample() {
        String original = "Message";
        CompletableFuture cf = CompletableFuture.completedFuture(original)
                .thenApplyAsync(s -> delayedUpperCase(s))
                .thenCombine(CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
                        (s1, s2) -> s1 + s2);
        assertEquals("MESSAGEmessage", cf.join());
    }*/

    @Test
    public void thenComposeExample() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture.completedFuture(original).thenApply(s -> delayedUpperCase(s));
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture(original).thenApply(s -> delayedLowerCase(s));
        CompletableFuture<String> result = cf.thenCompose(first -> cf2.thenApply(second -> first + second));
        assertEquals("MESSAGEmessage", result.join());

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));

        assertEquals("Hello World", completableFuture.join());
    }


    // AnyOF & AllOf
    @Test
    public void anyOfExample() {
        final StringBuilder result = new StringBuilder();
        final List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture> futures =
                messages.stream()
                        .map(msg -> CompletableFuture.completedFuture(msg).thenApply(s -> delayedUpperCase(s)))
                        .collect(Collectors.toList());

        CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()]))
                .whenComplete((msg, throwable) -> {
                    if(throwable == null) {
                        result.append(msg);
                    }
                });
        assertEquals(1, result.length());
        assertEquals("A", result.toString());
    }



    //***************** Either examples
    /**
     * Function & apply
     */
    @Test
    public void applyToEitherExample() {
        String original = "Message";
        CompletableFuture cf1 = CompletableFuture.completedFuture(original)
                .thenApplyAsync(CompletableFutureTest::delayedUpperCase);
        CompletableFuture cf2 = cf1.applyToEither(
                CompletableFuture.completedFuture(original).thenApplyAsync(CompletableFutureTest::delayedLowerCase),
                s -> s + " from applyToEither");

        String result = (String) cf2.join();
        System.out.println(result);
        assertTrue(result.endsWith(" from applyToEither"));
    }

    /**
     * Consumer & accept
     */
    @Test
    public void acceptEitherExample() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture cf = CompletableFuture.completedFuture(original)
                .thenApplyAsync(CompletableFutureTest::delayedUpperCase)
                .acceptEither(CompletableFuture.completedFuture(original).thenApplyAsync(CompletableFutureTest::delayedLowerCase),
                        s -> result.append(s).append("acceptEither"));
        cf.join();
        System.out.println(result);
        assertTrue("Result was empty", result.toString().endsWith("acceptEither"));
    }

    /**
     * runAfterBoth, thenAcceptBoth
     */
    @Test
    public void runAfterBothExample() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture(original)
                .thenApply(String::toUpperCase)
                .runAfterBoth(
                        CompletableFuture.completedFuture(original).thenApply(String::toLowerCase),
                        () -> result.append("done"));
        assertEquals("done", result.toString());
        assertTrue("Result was empty", result.length() > 0);
    }

    @Test
    public void thenAcceptBothExample() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture(original).thenApply(String::toUpperCase).thenAcceptBoth(
                CompletableFuture.completedFuture(original).thenApply(String::toLowerCase),
                (s1, s2) -> result.append(s1).append(s2));
        assertEquals("MESSAGEmessage", result.toString());
    }

    private static String delayedLowerCase(String s) {
        System.out.println("delayedLowerCase **** Thread - " + Thread.currentThread().getName());
        System.out.println("Is Thread deamon? " + Thread.currentThread().isDaemon());
        sleep(300);
        return s.toLowerCase();
    }

    private static String delayedUpperCase(String s) {
        System.out.println("delayedUpperCase **** Thread - " + Thread.currentThread().getName());
        System.out.println("Is Thread deamon? " + Thread.currentThread().isDaemon());
        sleep(300);
        return s.toUpperCase();
    }

}
