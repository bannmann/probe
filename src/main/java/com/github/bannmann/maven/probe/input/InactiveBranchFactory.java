package com.github.bannmann.maven.probe.input;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class InactiveBranchFactory
{
    private final NodeFactory nodeFactory;

    public Branch create(DependencyNode dependencyNode)
    {
        return Branch.builder()
            .edge(createInactiveEdge(dependencyNode))
            .node(createInactiveNode(dependencyNode))
            .build();
    }

    private Edge createInactiveEdge(DependencyNode dependencyNode)
    {
        Dependency dependency = dependencyNode.getDependency();
        return EdgeImpl.builder()
            .type(Edge.Type.INACTIVE)
            .scope(dependency.getScope())
            .optional(dependency.isOptional())
            .version(dependency.getArtifact().getVersion())
            .build();
    }

    private Node createInactiveNode(DependencyNode dependencyNode)
    {
        return nodeFactory.create(dependencyNode);
    }
}
