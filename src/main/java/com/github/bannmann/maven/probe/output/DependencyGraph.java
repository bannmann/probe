package com.github.bannmann.maven.probe.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

@RequiredArgsConstructor
final class DependencyGraph
{
    private final Graph targetGraph;

    public Dependency getRoot()
    {
        Node root = targetGraph.nodes().iterator().next();
        return Dependency.builder().target(root).graph(targetGraph).build();
    }

    public List<Dependency> getDependencies(Node node)
    {
        SetMultimap<Edge, Edge> edges = targetGraph.outEdges(node)
            .stream()
            .collect(Multimaps.toMultimap(edge -> edge.getActive().orElse(edge),
                edge -> edge,
                () -> MultimapBuilder.linkedHashKeys().hashSetValues().build()));

        List<Dependency> dependencyList = new ArrayList<>();
        for (Map.Entry<Edge, Collection<Edge>> edgeGroup : edges.asMap().entrySet())
        {
            dependencyList.add(createDependency(edgeGroup));
        }
        return dependencyList;
    }

    private Dependency createDependency(Map.Entry<Edge, Collection<Edge>> edgeGroup)
    {
        EnumMap<Edge.Type, Edge> edges = new EnumMap<>(Edge.Type.class);
        edgeGroup.getValue().forEach(edge -> edges.put(edge.getType(), edge));
        return Dependency.builder().edges(null).edges(edges).target(getTargetNode(edgeGroup.getKey())).build();
    }

    private Node getTargetNode(Edge edge)
    {
        return targetGraph.incidentNodes(edge).target();
    }
}
