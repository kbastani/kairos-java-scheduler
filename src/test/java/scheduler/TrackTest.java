package scheduler;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class TrackTest {
    @Test
    public void deliver() throws Exception {
        SignalRepository signalRepository = new SignalRepository();

        Cart<Double>[] carts = new Cart[]{};
        ExpandingResource<Cart<Double>, Double> resource =
                new ExpandingResource<>(carts, Cart::new, signalRepository);

        Track<Double> track = new Track<>(resource);

        // Creates a single-threaded track that delivers four orders over two seconds
        List<Order<Double>> orders = DoubleStream.of(1.0, 2.0, 3.0, 4.0)
                .mapToObj(k -> {
                    Resource<Double> r = Resource.of(k);
                    return new Order<>((long) r.hashCode(), ((Math.round(k)) * 5L), r);
                }).collect(Collectors.toList());

        orders.forEach(track::schedule);

        while (!track.isEmpty()) {
            System.out.println(Arrays.toString(track.deliver().stream().flatMap(Collection::stream)
                    .collect(Collectors.toList()).toArray(Double[]::new)));
            Thread.sleep(100);
        }
    }

    class SignalRepository implements StreamingRepository<Cart<Double>, Double> {

        private final Map<Long, Cart<Double>> repo = new HashMap<>();

        @Override
        public Cart<Double> getById(Long id) {
            return repo.get(id);
        }

        @Override
        public void save(Cart<Double> item) {
            repo.put(item.getId(), item);
        }

        @Override
        public Order<Double> saveOrder(Order<Double> order) {
            return order;
        }

        @Override
        public void remove(Long id) {
            repo.remove(id);
        }

        @Override
        public boolean isEmpty() {
            return repo.isEmpty();
        }
    }
}