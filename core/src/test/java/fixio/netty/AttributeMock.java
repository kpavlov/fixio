package fixio.netty;


import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicReference;

public class AttributeMock<T> implements Attribute<T> {

    private final AtomicReference<T> holder = new AtomicReference<>();

    @Override
    public AttributeKey<T> key() {
        return null;
    }

    @Override
    public T get() {
        return holder.get();
    }

    @Override
    public void set(T value) {
        this.holder.set(value);
    }

    @Override
    public T getAndSet(T value) {
        return holder.getAndSet(value);
    }

    @Override
    public T setIfAbsent(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getAndRemove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean compareAndSet(T oldValue, T newValue) {
        return holder.compareAndSet(oldValue, newValue);
    }

    @Override
    public void remove() {
        holder.set(null);
    }
}
