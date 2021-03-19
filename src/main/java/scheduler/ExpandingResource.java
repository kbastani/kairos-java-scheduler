package scheduler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A resource is an immutable container of items that incrementally returns a subset of the initial buffer. This
 * resource is safe for concurrent transactions. An expanding resource is a {@link Resource} that automatically
 * grows by instantiating new resources as the position exceeds the current container size. This allows for
 * lazily loading resources from a source by id. A source is an indexed map of resources that may not be
 * equally distributed neatly within a resource container. An expanding resource will instantiate a default
 * object when a source has an empty item at a specific index.
 * <p>
 * An expanding resource is memory efficient, and is initialized with a streaming repository.
 *
 * @param <T> is the type of resource
 */
public class ExpandingResource<T, V> extends Resource<T> {

	private ArrayList<T> expandingBuffer = new ArrayList<>();
	private Function<Long, T> factory;
	private StreamingRepository<T, V> repository;

	public ExpandingResource(Integer size, Integer position, T[] buffer, Function<Long, T> factory, StreamingRepository<T, V> repository) {
		super(size, position, buffer);
		this.repository = repository;
		expandingBuffer.addAll(Stream.of(buffer).collect(Collectors.toList()));
		this.factory = factory;
	}

	public ExpandingResource(T[] buffer, Function<Long, T> factory, StreamingRepository<T, V> repository) {
		super(0, 0, buffer);
		expandingBuffer.addAll(Stream.of(buffer).collect(Collectors.toList()));
		this.factory = factory;
		this.repository = repository;
	}

	public StreamingRepository<T, V> getRepository() {
		return repository;
	}

	public Function<Long, T> getFactory() {
		return factory;
	}

	@Override
	public ArrayList<T> take(Integer limit) {
		ArrayList<T> result;
		synchronized (this) {
			int overflow = Math.max(0, (position + limit) - size);

			if (overflow > 0) {
				// Expand the buffer
				expandingBuffer.addAll(LongStream.range(size, size + overflow)
						.mapToObj(i -> {
							T item = repository.getById(i);
							if (item == null)
								item = factory.apply(i);
							return item;
						})
						.collect(Collectors.toList()));
				size += overflow;
			}

			int end = Math.min(position + limit, size);
			int start = position;
			position = end;
			result = IntStream.range(start, end)
					.mapToObj(i -> expandingBuffer.get(i - start))
					.peek(i -> expandingBuffer.remove(i))
					.collect(Collectors.toCollection(ArrayList::new));

			if (Objects.equals(position, size))
				state = ResourceState.EXHAUSTED;
		}
		return result;
	}
}
