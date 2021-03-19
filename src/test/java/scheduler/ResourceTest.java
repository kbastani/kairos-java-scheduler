package scheduler;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ResourceTest {
    @Test
    public void takeFiveAndIncrementPosition() throws Exception {

        Resource<Integer> resource = new Resource<>(10, 0, IntStream.range(0, 10).boxed()
                .toArray(Integer[]::new));
        Integer[] actual = new Integer[]{0, 1, 2, 3, 4};
        Integer actualPos = 5;

        Assert.assertArrayEquals(resource.take(5).toArray(), actual);
        Assert.assertEquals(resource.getPosition(), actualPos);
    }

    @Test
    public void asyncIncrementalExhaustion() throws Exception {
        Resource<Integer> resource = new Resource<>(10, 0, IntStream.range(0, 10).boxed()
                .toArray(Integer[]::new));
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Integer[]> buffer = new ArrayList<>();
        List<Callable<Boolean>> invocations = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            invocations.add(() -> {
                System.out.println("Item: " + finalI);
                return buffer.add((Integer[]) resource.take(1).toArray());
            });
        }

        executor.invokeAll(invocations);
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);

        Integer[] actual = buffer.stream().flatMap(Stream::of).toArray(Integer[]::new);
        Integer[] expected = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        Thread.sleep(100);

        Assert.assertArrayEquals(expected, actual);
        Assert.assertEquals(resource.getState(), ResourceState.EXHAUSTED);
    }
}