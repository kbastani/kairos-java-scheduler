package scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements StreamingRepository<Cart<Double>, Double> {

    private final Map<Long, Cart<Double>> repo =  new ConcurrentHashMap<>();

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
