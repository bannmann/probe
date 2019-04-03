package com.github.bannmann.maven.probe.input;

import lombok.NonNull;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;

import com.github.bannmann.maven.probe.model.Node;

final class NodeFactory
{
    public Node create(DependencyNode dependencyNode)
    {
        return create(dependencyNode.getArtifact());
    }

    public Node create(@NonNull Artifact artifact)
    {
        return new NodeImpl(artifact);
    }
}
