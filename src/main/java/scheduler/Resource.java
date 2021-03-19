package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A resource is an immutable container of items that incrementally returns a subset of the initial buffer. This
 * resource is safe for concurrent transactions.
 *
 * @param <T> is the type of resource
 */
public class Resource<T> {

    protected Integer size;
    protected Integer position;
    protected T[] buffer;
    protected ResourceState state;
    protected Function<Void, T[]> fetchBuffer = (a) -> buffer;

    public Resource() {
        state = ResourceState.FULL;
    }

    public Resource(Integer size, Integer position, T[] buffer) {

        state = ResourceState.FULL;

        this.size = size;
        this.position = position;
        this.buffer = buffer;

        if (size > 0) {
        } else {
            if (size == 0) {
                if (Objects.equals(position, size)) {
                    state = ResourceState.EXHAUSTED;
                }
            }
        }
    }

    public Resource(Integer size, Integer position, Function<Void, T[]> fetchBuffer) {
        this(size, position, (T[]) null);
        this.fetchBuffer = fetchBuffer;
    }

    /**
     * Gets the current state of the resource. When the buffer position reaches the size of the container, the
     * state transitions to EXHAUSTED.
     *
     * @return the state of the resources inside the container
     */
    public ResourceState getState() {
        return state;
    }

    /**
     * Gets the size of the container.
     *
     * @return returns the size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Gets the current position of the resource, between 0 and the container size.
     *
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    public T[] getBuffer() {
        return buffer.clone();
    }

    /**
     * Takes the specified number of items from the resource buffer and increments the position until the resource
     * buffer is empty.
     *
     * @param limit is the number of items to take from the resource buffer
     * @return a subset of the buffer between the current position and the limit
     */
    public ArrayList<T> take(Integer limit) {
        ArrayList<T> result;
        if (state == ResourceState.FULL) {
            // Load resource from source function
            buffer = fetchBuffer.apply(null);
            if(size == -1)
            	size = buffer.length;
            state = ResourceState.NOT_EMPTY;
        }

        int end = Math.min(position + limit, size);
        int start = position;
        position = end;
        result = Arrays.stream(Arrays.copyOfRange(buffer, start, position))
                .collect(Collectors.toCollection(ArrayList::new));

        if (position >= size) {
            state = ResourceState.EXHAUSTED;
        } else if (position > 0 && position < size) {
            state = ResourceState.NOT_EMPTY;
        }

        return result;
    }

    public void setFetchBuffer(Function<Void, T[]> fetchBuffer) {
        this.fetchBuffer = fetchBuffer;
    }

    @SafeVarargs
    public static <T> Resource<T> of(T... items) {
        return new Resource<>(items.length, 0, items);
    }

    public static <T> Resource<T> of(Function<Void, T[]> fetch, int size) {
        return new Resource<>(size, 0, fetch);
    }
}
