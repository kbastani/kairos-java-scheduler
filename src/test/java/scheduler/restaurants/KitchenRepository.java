package scheduler.restaurants;

import scheduler.Cart;
import scheduler.Order;
import scheduler.StreamingRepository;

import java.util.HashMap;
import java.util.Map;

public class KitchenRepository implements StreamingRepository<Cart<OrderDelivery>, OrderDelivery> {

    private final Map<Long, Cart<OrderDelivery>> repo = new HashMap<>();

    @Override
    public Cart<OrderDelivery> getById(Long id) {
        return repo.get(id);
    }

    @Override
    public void save(Cart<OrderDelivery> item) {
        repo.put(item.getId(), item);
    }

    @Override
    public Order<OrderDelivery> saveOrder(Order<OrderDelivery> order) {
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
