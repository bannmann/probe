package com.github.bannmann.maven.probe.output;

import java.util.Iterator;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ContextIterables
{
    private static final class ContextIterator<I extends Iterable<E>, E> implements Iterator<Iteration<E>>
    {
        private final Iterator<E> target;
        private boolean first = true;

        private ContextIterator(I iterable)
        {
            this.target = iterable.iterator();
        }

        @Override
        public boolean hasNext()
        {
            return target.hasNext();
        }

        @Override
        public Iteration<E> next()
        {
            Iteration<E> result = IterationImpl.<E>builder().element(target.next())
                .first(first)
                .last(!target.hasNext())
                .build();
            first = false;
            return result;
        }
    }

    @Value
    @Builder
    private final class IterationImpl<T> implements Iteration<T>
    {
        private T element;
        private boolean first;
        private boolean last;
    }

    @Value
    @RequiredArgsConstructor
    private final class IterableWrapper<I extends Iterable<E>, E> implements Iterable<Iteration<E>>
    {
        private I target;

        @Override
        public Iterator<Iteration<E>> iterator()
        {
            return new ContextIterator<>(target);
        }
    }

    public interface Iteration<T>
    {
        T getElement();

        boolean isFirst();

        boolean isLast();
    }

    public <I extends Iterable<E>, E> Iterable<Iteration<E>> create(I target)
    {
        return new IterableWrapper<>(target);
    }
}
