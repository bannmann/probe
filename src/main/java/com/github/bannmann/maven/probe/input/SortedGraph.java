package com.github.bannmann.maven.probe.input;

import java.util.Comparator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.graph.Network;

@Slf4j
final class SortedGraph extends DelegatingGraph
{
    private final Comparator<Edge> edgeComparator;
    private final Comparator<Node> nodeComparator;

    public SortedGraph(Network<Node, Edge> graph)
    {
        super(graph);

        ImmutableList<Edge> edgeOrder = ImmutableList.copyOf(graph.edges());
        edgeComparator = Comparator.comparing(edgeOrder::indexOf);

        ImmutableList<Node> nodeOrder = ImmutableList.copyOf(graph.nodes());
        nodeComparator = Comparator.comparing(nodeOrder::indexOf);
    }

    @Override
    public Set<Node> adjacentNodes(Node node)
    {
        return sortNodes(super.adjacentNodes(node));
    }

    private ImmutableSortedSet<Node> sortNodes(Set<Node> original)
    {
        return ImmutableSortedSet.copyOf(nodeComparator, original);
    }

    @Override
    public Set<Node> predecessors(Node node)
    {
        return sortNodes(super.predecessors(node));
    }

    @Override
    public Set<Node> successors(Node node)
    {
        return sortNodes(super.successors(node));
    }

    @Override
    public Set<Edge> incidentEdges(Node node)
    {
        return sortEdges(super.incidentEdges(node));
    }

    private ImmutableSortedSet<Edge> sortEdges(Set<Edge> original)
    {
        return ImmutableSortedSet.copyOf(edgeComparator, original);
    }

    @Override
    public Set<Edge> inEdges(Node node)
    {
        return sortEdges(super.inEdges(node));
    }

    @Override
    public Set<Edge> outEdges(Node node)
    {
        return sortEdges(super.outEdges(node));
    }

    @Override
    public Set<Edge> adjacentEdges(Edge edge)
    {
        return sortEdges(super.adjacentEdges(edge));
    }

    @Override
    public Set<Edge> edgesConnecting(Node nodeU, Node nodeV)
    {
        return sortEdges(super.edgesConnecting(nodeU, nodeV));
    }
}
