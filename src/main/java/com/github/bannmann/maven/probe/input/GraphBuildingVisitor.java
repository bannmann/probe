package com.github.bannmann.maven.probe.input;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class GraphBuildingVisitor implements DependencyVisitor
{
    private final MutableNetwork<Node, Edge> network = NetworkBuilder.directed()
        .allowsParallelEdges(true)
        .edgeOrder(ElementOrder.insertion())
        .nodeOrder(ElementOrder.insertion())
        .build();
    private final Deque<Node> parentNodes = new ArrayDeque<>();

    private final ActiveBranchFactory activeBranchFactory;
    private final MediatedBranchFactory mediatedBranchFactory;
    private final ManagedBranchFactory managedBranchFactory;
    private final OriginalBranchFactory originalBranchFactory;

    private Graph graph;

    public boolean visitEnter(DependencyNode dependencyNode)
    {
        Branch activeBranch = activeBranchFactory.create(dependencyNode);
        getParentNode().ifPresent(saveRelevantBranches(dependencyNode, activeBranch));
        setParentNode(activeBranch);

        return true;
    }

    private Consumer<Node> saveRelevantBranches(DependencyNode dependencyNode, Branch activeBranch)
    {
        return currentNode -> saveRelevantBranches(currentNode, dependencyNode, activeBranch);
    }

    private void saveRelevantBranches(Node sourceNode, DependencyNode dependencyNode, Branch activeBranch)
    {
        Consumer<Branch> saveBranch = branch -> saveBranch(sourceNode, branch);

        saveBranch.accept(activeBranch);
        originalBranchFactory.create(dependencyNode, activeBranch).ifPresent(saveBranch);
        mediatedBranchFactory.create(dependencyNode, activeBranch).ifPresent(saveBranch);
        managedBranchFactory.create(dependencyNode, activeBranch).ifPresent(saveBranch);
    }

    private void saveBranch(Node sourceNode, Branch branch)
    {
        Node targetNode = branch.getNode();
        network.addEdge(sourceNode, targetNode, branch.getEdge());
    }

    private void setParentNode(Branch activeBranch)
    {
        parentNodes.push(activeBranch.getNode());
    }

    private Optional<Node> getParentNode()
    {
        return Optional.ofNullable(parentNodes.peek());
    }

    public boolean visitLeave(DependencyNode dependencyNode)
    {
        parentNodes.pop();

        if (parentNodes.isEmpty())
        {
            graph = new SortedGraph(network);
        }

        return true;
    }

    public Graph getGraph()
    {
        if (graph == null)
        {
            throw new IllegalStateException();
        }
        return graph;
    }
}
