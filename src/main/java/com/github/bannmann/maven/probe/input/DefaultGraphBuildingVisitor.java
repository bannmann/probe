package com.github.bannmann.maven.probe.input;

import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;
import com.github.bannmann.maven.probe.util.DequeStack;
import com.google.common.graph.MutableNetwork;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class DefaultGraphBuildingVisitor implements DependencyVisitor
{
    private final DequeStack parentNodes = new DequeStack();

    private final ActiveBranchFactory activeBranchFactory;
    private final MediatedBranchFactory mediatedBranchFactory;
    private final ManagedBranchFactory managedBranchFactory;
    private final OriginalBranchFactory originalBranchFactory;

    private MutableNetwork<Node, Edge> network;

    public void initialize(MutableNetwork<Node, Edge> network)
    {
        this.network = network;
    }

    public boolean visitEnter(DependencyNode dependencyNode)
    {
        Branch activeBranch = activeBranchFactory.create(dependencyNode);
        getParentNode().ifPresent(saveRelevantBranches(dependencyNode, activeBranch));
        setNewParentNode(activeBranch);

        return true;
    }

    private Optional<Node> getParentNode()
    {
        return parentNodes.current();
    }

    private void setNewParentNode(Branch activeBranch)
    {
        Node newParentNode = activeBranch.getNode();
        parentNodes.enter(newParentNode);

        /*
         * Make sure each node we enter is added to the network. This is important for root nodes without dependencies,
         * or those that have only 'provided' or 'test' dependencies.
         */
        network.addNode(newParentNode);
    }

    private Consumer<Node> saveRelevantBranches(DependencyNode dependencyNode, Branch activeBranch)
    {
        return parentNode -> saveRelevantBranches(parentNode, dependencyNode, activeBranch);
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

    public boolean visitLeave(DependencyNode dependencyNode)
    {
        parentNodes.leave();
        return true;
    }
}
