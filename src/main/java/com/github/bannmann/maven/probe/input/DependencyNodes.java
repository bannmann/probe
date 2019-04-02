package com.github.bannmann.maven.probe.input;

import java.util.Optional;

import javax.inject.Inject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

import com.github.bannmann.maven.probe.model.Node;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class DependencyNodes
{
    private final NodeFactory nodeFactory;

    public Optional<Node> getConflictResolutionWinner(@NonNull DependencyNode dependencyNode)
    {
        return getWinningNode(dependencyNode).map(nodeFactory::create)
            .filter(node -> !node.equals(nodeFactory.create(dependencyNode)));
    }

    private Optional<DependencyNode> getWinningNode(DependencyNode dependencyNode)
    {
        return Optional.ofNullable((DependencyNode) dependencyNode.getData().get(ConflictResolver.NODE_DATA_WINNER));
    }

    public boolean hasResolvedConflicts(@NonNull DependencyNode dependencyNode)
    {
        return getConflictResolutionWinner(dependencyNode).isPresent();
    }
}
