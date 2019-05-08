package com.github.bannmann.maven.probe.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

public final class DequeStack<E>
{
    private final Deque<E> contents = new ArrayDeque<>();

    public void enter(E element)
    {
        contents.push(element);
    }

    public Optional<E> current()
    {
        return Optional.ofNullable(contents.peek());
    }

    public boolean contains(E element)
    {
        return contents.contains(element);
    }

    public void leave()
    {
        contents.pop();
    }

    public boolean tryLeave(E element)
    {
        if (Objects.equals(contents.peek(), element))
        {
            contents.pop();
            return true;
        }

        return false;
    }

    public boolean isEmpty()
    {
        return contents.isEmpty();
    }
}
