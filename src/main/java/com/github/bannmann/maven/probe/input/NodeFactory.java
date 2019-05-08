package com.github.bannmann.maven.probe.input;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import lombok.NonNull;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;

import com.github.bannmann.maven.probe.model.Node;

@Singleton
final class NodeFactory
{
    private Map<Artifact, Node> nodes = new HashMap<>();

    public Node create(DependencyNode dependencyNode)
    {
        return create(dependencyNode.getArtifact());
    }

    public Node create(@NonNull Artifact artifact)
    {
        return nodes.computeIfAbsent(artifact, NodeImpl::new);
    }
}
