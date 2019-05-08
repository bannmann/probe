package com.github.bannmann.maven.probe.input;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
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

    @Inject
    private DependencyResolver dependencyResolver;

    public Graph getGraph() throws DependencyCollectionException, DependencyResolutionException
    {
        MutableNetwork<Node, Edge> network = createEmptyNetwork();

        resolve(network);

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

    private void resolve(MutableNetwork<Node, Edge> targetNetwork) throws DependencyResolutionException
    {
        DependencyResolutionResult resolutionResult = dependencyResolver.resolve(project);

        DefaultGraphBuildingVisitor visitor = defaultGraphBuildingVisitorProvider.get();
        visitor.initialize(targetNetwork);
        resolutionResult.getDependencyGraph().accept(visitor);
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
            collect(artifact, network);
        }
    }

    private Predicate<Node> hasNoActiveEdges(MutableNetwork<Node, Edge> network)
    {
        return node -> network.incidentEdges(node).stream().noneMatch(edge -> edge.getType() == Edge.Type.ACTIVE);
    }

    private void collect(Artifact rootArtifact, MutableNetwork<Node, Edge> targetNetwork)
        throws DependencyCollectionException
    {
        CollectResult collectResult = dependencyCollector.collect(rootArtifact);

        InactiveGraphBuildingVisitor visitor = inactiveGraphBuildingVisitorProvider.get();
        visitor.initialize(targetNetwork);
        collectResult.getRoot().accept(visitor);
    }
}
