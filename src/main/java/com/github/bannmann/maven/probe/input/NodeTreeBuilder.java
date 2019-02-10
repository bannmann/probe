package com.github.bannmann.maven.probe.input;

import java.util.ArrayDeque;
import java.util.Deque;

import lombok.Getter;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

import com.github.bannmann.maven.probe.model.Attributes;
import com.github.bannmann.maven.probe.model.Node;

final class NodeTreeBuilder implements DependencyVisitor
{
    private final Deque<Node> stack = new ArrayDeque<>();

    @Getter
    private Node root;

    public boolean visitEnter(DependencyNode dependencyNode)
    {
        Node result = createNode(dependencyNode);

        if (!stack.isEmpty())
        {
            stack.peek().add(result);
        }
        stack.push(result);

        return true;
    }

    private Node createNode(DependencyNode dependencyNode)
    {
        Node result = new Node();

        Artifact a = dependencyNode.getArtifact();
        result.setArtifact(a);

        Dependency dependency = dependencyNode.getDependency();
        Attributes attributes = getAttributes(a, dependency);
        result.setAttributes(attributes);

        Attributes premanagedAttributes = getPremanagedAttributes(dependencyNode);
        if (!premanagedAttributes.equals(attributes))
        {
            result.setPremanagedAttributes(premanagedAttributes);
        }

        Artifact winningArtifact = getWinningArtifact(dependencyNode);
        if (winningArtifact != null && !ArtifactIdUtils.equalsId(a, winningArtifact))
        {
            result.setWinningArtifact(winningArtifact);
        }
        return result;
    }

    private Attributes getAttributes(Artifact artifact, Dependency d)
    {
        return Attributes.builder()
            .scope(d.getScope())
            .optional(d.isOptional())
            .version(artifact.getBaseVersion())
            .build();
    }

    private Attributes getPremanagedAttributes(DependencyNode dependencyNode)
    {
        return Attributes.builder()
            .scope(DependencyManagerUtils.getPremanagedScope(dependencyNode))
            .optional(DependencyManagerUtils.getPremanagedOptional(dependencyNode))
            .version(DependencyManagerUtils.getPremanagedVersion(dependencyNode))
            .build();
    }

    private Artifact getWinningArtifact(DependencyNode dependencyNode)
    {
        Artifact result = null;
        DependencyNode winner = (DependencyNode) dependencyNode.getData().get(ConflictResolver.NODE_DATA_WINNER);
        if (winner != null)
        {
            result = winner.getArtifact();
        }
        return result;
    }

    public boolean visitLeave(DependencyNode dependencyNode)
    {
        Node node = stack.pop();
        if (stack.isEmpty())
        {
            root = node;
        }

        return true;
    }
}
