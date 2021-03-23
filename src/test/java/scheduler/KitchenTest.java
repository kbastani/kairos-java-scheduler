package scheduler;

import org.junit.Assert;
import org.junit.Test;
import scheduler.restaurants.Kitchen;
import scheduler.restaurants.OrderDelivery;

import java.util.Arrays;
import java.util.Collection;

public class KitchenTest {

    @Test
    public void testChefDelivery() {
        Kitchen kitchen = new Kitchen();
        kitchen.schedule(new Order<>(1L, 0L, Resource.of(new OrderDelivery())));
        kitchen.schedule(new Order<>(2L, 1L, Resource.of(new OrderDelivery())));
        kitchen.schedule(new Order<>(3L, 2L, Resource.of(new OrderDelivery())));
        kitchen.schedule(new Order<>(4L, 3L, Resource.of(new OrderDelivery())));

        System.out.println(Arrays.toString(kitchen.deliver().stream()
                .flatMap(Collection::stream).toArray()));

        System.out.println(Arrays.toString(kitchen.deliver().stream()
                .flatMap(Collection::stream).toArray()));

        System.out.println(Arrays.toString(kitchen.deliver().stream()
                .flatMap(Collection::stream).toArray()));

        System.out.println(Arrays.toString(kitchen.deliver().stream()
                .flatMap(Collection::stream).toArray()));

        Assert.assertTrue(kitchen.isEmpty());
    }
}
