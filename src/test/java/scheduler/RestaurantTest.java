package scheduler;

import org.junit.Test;
import scheduler.restaurants.Restaurant;
import scheduler.restaurants.config.RestaurantConfigProperties;

public class RestaurantTest {

    @Test
    public void testRestaurantDelivery() throws InterruptedException {
        Restaurant restaurant = Restaurant.from(new RestaurantConfigProperties(1, 500L));
        restaurant.open();
        Thread.sleep(5000L);
        restaurant.close();
    }
}
