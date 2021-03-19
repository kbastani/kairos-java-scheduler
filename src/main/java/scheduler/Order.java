package scheduler;

import java.util.Objects;

/**
 * An order is a container of a variable-length resource that has been committed to a sequence of {@link Cart}s.
 */
public class Order<T> {

    private Long id;
    private Long deliveryTime = 0L;
    private Resource<T> resource;
    private ResourceState state;

    public Order() {
        state = ResourceState.FULL;
    }

    public Order(Long id, Resource<T> resource) {
        this();
        this.id = id;
        this.resource = resource;
    }

    public Order(Long id, Long deliveryTime, Resource<T> resource) {
        this();
        this.id = id;
        this.deliveryTime = deliveryTime;
        this.resource = resource;
    }

    public Long getId() {
        return id;
    }

    public Resource<T> getResource() {
        return resource;
    }

    public Long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public ResourceState getState() {
        return state;
    }

    public void setState(ResourceState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order<?> order = (Order<?>) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
