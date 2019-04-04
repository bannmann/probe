package com.github.bannmann.maven.probe.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import com.github.bannmann.maven.probe.model.Node;

public final class NodeStack
{
    private final Deque<Node> contents = new ArrayDeque<>();

    public void enter(Node node)
    {
        contents.push(node);
    }

    public Optional<Node> current()
    {
        return Optional.ofNullable(contents.peek());
    }

    public boolean contains(Node node)
    {
        return contents.contains(node);
    }

    public void leave()
    {
        contents.pop();
    }
}
