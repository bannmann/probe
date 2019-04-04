package com.github.bannmann.maven.probe.input;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

public final class DependencyGraphBuilder
{
    @Inject
    @Named("includeInactive")
    private boolean includeInactive;

    @Inject
    private MavenProject project;

    @Inject
    private Provider<DefaultGraphBuildingVisitor> defaultGraphBuildingVisitorProvider;

    @Inject
    private Provider<InactiveGraphBuildingVisitor> inactiveGraphBuildingVisitorProvider;

    @Inject
    private DependencyCollector dependencyCollector;

    public Graph getGraph() throws DependencyCollectionException
    {
        DefaultArtifact rootArtifact = new DefaultArtifact(project.getArtifact().toString());

        MutableNetwork<Node, Edge> network = createEmptyNetwork();

        applyVisitor(rootArtifact, defaultGraphBuildingVisitorProvider, network);

        if (includeInactive)
        {
            addInactiveDependencies(network);
        }

        return new SortedGraph(network);
    }

    private MutableNetwork<Node, Edge> createEmptyNetwork()
    {
        return NetworkBuilder.directed()
            .allowsParallelEdges(true)
            .edgeOrder(ElementOrder.insertion())
            .nodeOrder(ElementOrder.insertion())
            .build();
    }

    private void applyVisitor(
        Artifact rootArtifact,
        Provider<? extends GraphBuildingVisitor> visitorProvider,
        MutableNetwork<Node, Edge> targetNetwork) throws DependencyCollectionException
    {
        CollectResult collectResult = dependencyCollector.collect(rootArtifact);

        GraphBuildingVisitor visitor = visitorProvider.get();
        visitor.initialize(targetNetwork);
        collectResult.getRoot().accept(visitor);
    }

    private void addInactiveDependencies(MutableNetwork<Node, Edge> network) throws DependencyCollectionException
    {
        List<Artifact> inactiveArtifacts = network.nodes()
            .stream()
            .filter(hasNoActiveEdges(network))
            .map(Node::getArtifact)
            .collect(Collectors.toList());

        // Loop over inactive artifacts separately to avoid concurrent modification exceptions
        for (Artifact artifact : inactiveArtifacts)
        {
            applyVisitor(artifact, inactiveGraphBuildingVisitorProvider, network);
        }
    }

    private Predicate<Node> hasNoActiveEdges(MutableNetwork<Node, Edge> network)
    {
        return node -> network.incidentEdges(node).stream().noneMatch(edge -> edge.getType() == Edge.Type.ACTIVE);
    }
}
