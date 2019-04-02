package com.github.bannmann.maven.probe.input;

import java.util.Optional;

import javax.inject.Inject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.eclipse.aether.graph.DependencyNode;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class MediatedBranchFactory
{
    private final DependencyNodes dependencyNodes;

    public Optional<Branch> create(@NonNull DependencyNode dependencyNode, Branch activeBranch)
    {
        activeBranch.verifyActive();

        return createNode(dependencyNode).map(node -> createBranch(node, activeBranch.getEdge()));
    }

    private Optional<Node> createNode(@NonNull DependencyNode dependencyNode)
    {
        return dependencyNodes.getConflictResolutionWinner(dependencyNode);
    }

    private Branch createBranch(Node node, Edge activeEdge)
    {
        Edge edge = createEdge(node, activeEdge);
        return Branch.builder().node(node).edge(edge).build();
    }

    private Edge createEdge(Node node, Edge activeEdge)
    {
        String version = node.getArtifact().getVersion();
        return EdgeImpl.builder().type(Edge.Type.MEDIATED).version(version).active(activeEdge).build();
    }
}
