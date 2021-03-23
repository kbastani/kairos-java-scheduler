package scheduler.restaurants;

import java.util.UUID;

public class OrderDelivery {

    private final Long id;

    public OrderDelivery() {
        id = (long)UUID.randomUUID().hashCode();
    }

    public OrderDelivery(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "KitchenOrder{" +
                "id=" + id +
                '}';
    }
}
