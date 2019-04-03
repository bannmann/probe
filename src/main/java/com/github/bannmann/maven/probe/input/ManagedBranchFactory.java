package com.github.bannmann.maven.probe.input;

import java.util.Optional;

import javax.inject.Inject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class ManagedBranchFactory
{
    private static final EdgeImpl.EdgeImplBuilder EMPTY_EDGE_BUILDER = EdgeImpl.builder().type(Edge.Type.MANAGED);

    private final DependencyNodes dependencyNodes;
    private final NodeFactory nodeFactory;

    public Optional<Branch> create(@NonNull DependencyNode dependencyNode, Branch activeBranch)
    {
        activeBranch.verifyActive();

        Optional<Node> managedNode = createManagedNode(dependencyNode);
        Optional<Edge> managedEdge = createManagedEdge(dependencyNode, activeBranch.getEdge(), managedNode);
        if (managedEdge.isPresent() || managedNode.isPresent())
        {
            // In case only the node was managed, create an empty edge
            Edge edge = managedEdge.orElseGet(EMPTY_EDGE_BUILDER::build);

            // In case only the edge was managed, copy the active node
            Node node = managedNode.orElse(activeBranch.getNode());

            return Optional.of(Branch.builder().edge(edge).node(node).build());
        }
        return Optional.empty();
    }

    private Optional<Edge> createManagedEdge(DependencyNode dependencyNode, Edge activeEdge, Optional<Node> managedNode)
    {
        EdgeImpl.EdgeImplBuilder result = EdgeImpl.builder().type(Edge.Type.MANAGED).active(activeEdge);

        String activeScope = activeEdge.getScope().orElseThrow(IllegalArgumentException::new);
        String premanagedScope = DependencyManagerUtils.getPremanagedScope(dependencyNode);
        if (premanagedScope != null)
        {
            result.scope(activeScope);
        }

        Boolean activeOptional = activeEdge.getOptional().orElseThrow(IllegalArgumentException::new);
        Boolean premanagedOptional = DependencyManagerUtils.getPremanagedOptional(dependencyNode);
        if (premanagedOptional != null)
        {
            result.optional(activeOptional);
        }

        managedNode.map(node -> node.getArtifact().getVersion()).ifPresent(result::version);

        return Optional.<Edge>of(result.build()).filter(edge -> edge.differs(EMPTY_EDGE_BUILDER.build()));
    }

    private Optional<Node> createManagedNode(DependencyNode dependencyNode)
    {
        return dependencyNodes.hasResolvedConflicts(dependencyNode)
            ? Optional.empty()
            : Optional.of(nodeFactory.create(dependencyNode));
    }
}
