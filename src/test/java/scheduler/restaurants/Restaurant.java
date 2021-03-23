package scheduler.restaurants;

import scheduler.Order;
import scheduler.Resource;
import scheduler.restaurants.config.RestaurantConfigProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The restaurant actor drives the state of an order forward after customer creation and until a driver pickup.
 * Each restaurant needs to maintain the state of its capacity to fulfill orders within a specified period of time.
 * <p>
 * For each of the restaurant actors, there are multiple variables that are responsible for determining the
 * supply-demand capacity for fulfilling an online order. In addition to online orders, a restaurant must also fulfill
 * new orders from dine-in customers. The initial state of a chef is dependent on an order fulfillment rate, which
 * represents the chef's average order fulfillment over a period of time. The number and fulfillment rate for each
 * chef actor should be initialized with the restaurant location.
 */
public class Restaurant {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final RestaurantConfigProperties properties;
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> orderScheduler;
    private Long orderCount = 0L;
    private Long restaurantId;
    private final Kitchen kitchen = new Kitchen();

    public Restaurant(RestaurantConfigProperties properties, Long restaurantId) {
        this.properties = properties;
        this.restaurantId = restaurantId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public ScheduledFuture<?> getOrderScheduler() {
        return orderScheduler;
    }

    public void setOrderScheduler(ScheduledFuture<?> orderScheduler) {
        this.orderScheduler = orderScheduler;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    public void newOrder() {
        Order<OrderDelivery> order = new Order<>(orderCount++, orderCount * 2,
                Resource.of(new OrderDelivery(orderCount)));
        log.info(order.toString());
        kitchen.schedule(order);
    }

    public void open() {
        this.close();

        scheduledExecutor.scheduleWithFixedDelay(() -> {
            if (!kitchen.isEmpty()) {
                ArrayList<ArrayList<OrderDelivery>> orders = kitchen.deliver();
                if (orders != null && orders.stream().mapToLong(Collection::size).sum() > 0)
                    log.info(Arrays.toString(orders.stream().flatMap(Collection::stream)
                            .collect(Collectors.toList()).toArray(OrderDelivery[]::new)));
            }
        }, properties.getNewOrderRate(), properties.getNewOrderRate(), TimeUnit.MILLISECONDS);

        this.setOrderScheduler(getScheduledExecutor().scheduleAtFixedRate(this::newOrder, properties.getNewOrderRate(),
                properties.getNewOrderRate(), TimeUnit.MILLISECONDS));
    }

    public void close() {
        if (orderScheduler != null) {
            orderScheduler.cancel(false);
        }
    }

    public static Restaurant from(RestaurantConfigProperties config) {
        return new Restaurant(config, (long) Math.abs(UUID.randomUUID().hashCode()));
    }
}
