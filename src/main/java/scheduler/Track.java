package scheduler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * A track is a lazily-loaded infinite stream of {@link Cart}s that are scheduled on the track.
 * The track is stream repository that marshals commitments back and forth between a data store. When a
 * {@link Cart} is delivered, the contents of the resources inside the cart are unpacked into a stream
 * of streams. The result of this stream of streams can be subscribed to by a function that applies
 * aggregate transformations.
 */
public class Track<T> {

    private boolean lazy = false;
    private ExpandingResource<Cart<T>, T> log;
    private Long position = 0L;
    private Long maxPosition = 0L;
    private Integer orders = 0;

    public Track(boolean lazy, ExpandingResource<Cart<T>, T> log) {
        this.lazy = lazy;
        this.log = log;
    }

    public void schedule(Order<T> order) {

        if (order.getState() == ResourceState.FULL) {
            orders++;
            // Make sure that the delivery time is in the future
            if (order.getDeliveryTime() < (position)) {
                order.setDeliveryTime(position + 1);
            }
        }

        Cart<T> cart = log.getRepository().getById(order.getDeliveryTime());
        if (cart == null) {
            cart = log.getFactory().apply(order.getDeliveryTime());
        }

        order = log.getRepository().saveOrder(order);
        cart.commit(order);
        log.getRepository().save(cart);
        //maxPosition = maxPosition < order.getDeliveryTime() ? order.getDeliveryTime() : maxPosition;

    }

    public ArrayList<ArrayList<T>> deliver() {
        ArrayList<ArrayList<T>> result = null;

        if (orders > 0) {
            LinkedBlockingQueue<Cart<T>> results = new LinkedBlockingQueue<>(log.take(1)
                    .stream().collect(Collectors.toUnmodifiableList()));
            Cart<T> item = results.poll();

            synchronized (Objects.requireNonNull(item)) {
                try {
                    result = item.deliver().collect(Collectors.toCollection(ArrayList::new));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // Overflow order resources to the next cart until the resource state is exhausted
            item.getOrders().stream().collect(Collectors.toUnmodifiableList()).forEach(o -> {
                if (o.getResource().getState() == ResourceState.EXHAUSTED) {
                    orders--;
                    o.setState(ResourceState.EXHAUSTED);
                } else {
                    o.setState(ResourceState.NOT_EMPTY);
                    o.setDeliveryTime(position + 1);
                    schedule(o);
                }
            });

            // Clean up
            log.getRepository().remove(item.getId());
            position++;
        }


        return result;
    }

    public Long getPosition() {
        return position;
    }

    public Long getMaxPosition() {
        return maxPosition;
    }

    public boolean isEmpty() {
        return log.getRepository().isEmpty();
    }

    public Integer getOrders() {
        return orders;
    }
}
