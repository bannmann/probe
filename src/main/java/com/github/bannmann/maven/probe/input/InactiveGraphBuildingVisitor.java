package com.github.bannmann.maven.probe.input;

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
final class InactiveGraphBuildingVisitor implements DependencyVisitor
{
    private final DequeStack<Node> parentNodes = new DequeStack<>();

    private final InactiveBranchFactory inactiveBranchFactory;

    private MutableNetwork<Node, Edge> network;

    public void initialize(MutableNetwork<Node, Edge> network)
    {
        this.network = network;
    }

    public boolean visitEnter(DependencyNode dependencyNode)
    {
        Branch branch = inactiveBranchFactory.create(dependencyNode);
        parentNodes.current().ifPresent(parentNode -> saveBranch(parentNode, branch));
        parentNodes.enter(branch.getNode());
        return true;
    }

    private void saveBranch(Node sourceNode, Branch branch)
    {
        Node targetNode = branch.getNode();
        if (!edgeExists(sourceNode, targetNode))
        {
            network.addEdge(sourceNode, targetNode, branch.getEdge());
        }
    }

    private boolean edgeExists(Node sourceNode, Node targetNode)
    {
        return network.nodes().contains(sourceNode) && network.successors(sourceNode).contains(targetNode);
    }

    public boolean visitLeave(DependencyNode dependencyNode)
    {
        parentNodes.leave();
        return true;
    }
}
