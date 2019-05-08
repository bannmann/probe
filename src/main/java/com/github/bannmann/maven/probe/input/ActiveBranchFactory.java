package com.github.bannmann.maven.probe.input;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class ActiveBranchFactory
{
    private final DependencyNodes dependencyNodes;
    private final NodeFactory nodeFactory;

    public Branch create(DependencyNode dependencyNode)
    {
        return Branch.builder().edge(createActiveEdge(dependencyNode)).node(createActiveNode(dependencyNode)).build();
    }

    private Edge createActiveEdge(DependencyNode dependencyNode)
    {
        Dependency dependency = dependencyNode.getDependency();
        if (dependency == null)
        {
            // dependencyNode is the root node of the graph. Synthesize an inbound edge for easier iteration.
            return EdgeImpl.builder().type(Edge.Type.ROOT).build();
        }

        return EdgeImpl.builder()
            .type(Edge.Type.ACTIVE)
            .scope(dependency.getScope())
            .optional(dependency.isOptional())
            .version(dependency.getArtifact().getVersion())
            .build();
    }

    private Node createActiveNode(DependencyNode dependencyNode)
    {
        return dependencyNodes.getConflictResolutionWinner(dependencyNode).orElse(nodeFactory.create(dependencyNode));
    }
}
