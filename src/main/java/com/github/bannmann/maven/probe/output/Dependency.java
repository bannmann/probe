package com.github.bannmann.maven.probe.output;

import java.util.EnumMap;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@Value
@Builder
final class Dependency
{
    @Getter(value = AccessLevel.NONE)
    EnumMap<Edge.Type, Edge> edges;

    Node target;

    Graph graph;

    public Optional<Edge> getEdge(Edge.Type type)
    {
        return (edges == null) ? Optional.empty() : Optional.ofNullable(edges.get(type));
    }
}
