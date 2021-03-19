package scheduler;

public interface StreamingRepository<T, V> {

    T getById(Long id);
    void save(T t);
    Order<V> saveOrder(Order<V> order);
    void remove(Long id);
    boolean isEmpty();
}
