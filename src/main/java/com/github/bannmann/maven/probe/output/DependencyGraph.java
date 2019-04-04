package com.github.bannmann.maven.probe.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

@RequiredArgsConstructor
final class DependencyGraph
{
    /**
     * Group of edges all coming from the same dependency.
     */
    @Value
    @Builder
    private static class EdgeGroup
    {
        /**
         * The active edge, i.e. the one that Maven selects for this dependency.
         */
        private final Edge key;

        /**
         * All related edges including the active one.
         */
        private final Collection<Edge> edges;
    }

    @Inject
    @Named("includeInactive")
    private boolean includeInactive;

    @Inject
    @Named("includeOptional")
    private boolean includeOptional;

    private Graph targetGraph;

    public void initialize(Graph targetGraph)
    {
        this.targetGraph = targetGraph;
    }

    public Dependency getRoot()
    {
        Node root = targetGraph.nodes().iterator().next();
        return Dependency.builder().target(root).graph(targetGraph).build();
    }

    public List<Dependency> getDependencies(Node node)
    {
        List<Dependency> result = new ArrayList<>();

        for (EdgeGroup edgeGroup : getEdgeGroups(node))
        {
            Dependency dependency = createDependency(edgeGroup);
            getPrimaryEdge(dependency).ifPresent(edge -> result.add(dependency));
            getOriginalEdge(dependency).ifPresent(edge -> result.add(createDependency(edge)));
        }

        return result;
    }

    private Set<EdgeGroup> getEdgeGroups(Node node)
    {
        SetMultimap<Edge, Edge> edges = targetGraph.outEdges(node).stream().collect(Multimaps.toMultimap(
            edge -> edge.getActive().orElse(edge),
            edge -> edge,
            () -> MultimapBuilder.linkedHashKeys().hashSetValues().build()));

        return edges.asMap()
            .entrySet()
            .stream()
            .map(edgeCollectionEntry -> EdgeGroup.builder()
                .key(edgeCollectionEntry.getKey())
                .edges(edgeCollectionEntry.getValue())
                .build())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Dependency createDependency(EdgeGroup edgeGroup)
    {
        EnumMap<Edge.Type, Edge> edges = new EnumMap<>(Edge.Type.class);
        edgeGroup.getEdges().forEach(edge -> edges.put(edge.getType(), edge));
        return Dependency.builder().edges(edges).target(getTargetNode(edgeGroup.getKey())).build();
    }

    private Node getTargetNode(Edge edge)
    {
        return targetGraph.incidentNodes(edge).target();
    }

    private Optional<Edge> getPrimaryEdge(Dependency dependency)
    {
        return dependency.getPrimaryEdge().filter(edge -> !isOptional(edge) || includeOptional);
    }

    private Boolean isOptional(Edge edge)
    {
        return edge.getOptional().orElse(false);
    }

    private Optional<Edge> getOriginalEdge(Dependency dependency)
    {
        return dependency.getEdge(Edge.Type.ORIGINAL).filter(edge -> includeInactive);
    }

    private Dependency createDependency(Edge edge)
    {
        EnumMap<Edge.Type, Edge> edges = new EnumMap<>(Edge.Type.class);
        edges.put(edge.getType(), edge);
        return Dependency.builder().edges(edges).target(getTargetNode(edge)).build();
    }
}
