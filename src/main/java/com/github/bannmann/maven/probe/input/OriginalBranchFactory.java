package com.github.bannmann.maven.probe.input;

import java.util.Optional;

import javax.inject.Inject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class OriginalBranchFactory
{
    private final NodeFactory nodeFactory;

    public Optional<Branch> create(@NonNull DependencyNode dependencyNode, Branch activeBranch)
    {
        activeBranch.verifyActive();

        Node originalNode = createOriginalNode(dependencyNode);
        Edge originalEdge = createOriginalEdge(dependencyNode, activeBranch.getEdge(), originalNode);
        Branch originalBranch = Branch.builder().edge(originalEdge).node(originalNode).build();

        return Optional.of(originalBranch).filter(branch -> branch.differs(activeBranch));
    }

    private Edge createOriginalEdge(DependencyNode dependencyNode, Edge activeEdge, Node originalNode)
    {
        EdgeImpl.EdgeImplBuilder result = EdgeImpl.toBuilder(activeEdge).type(Edge.Type.ORIGINAL).active(activeEdge);

        String premanagedScope = DependencyManagerUtils.getPremanagedScope(dependencyNode);
        if (premanagedScope != null)
        {
            result.scope(premanagedScope);
        }

        Boolean premanagedOptional = DependencyManagerUtils.getPremanagedOptional(dependencyNode);
        if (premanagedOptional != null)
        {
            result.optional(premanagedOptional);
        }

        result.version(originalNode.getArtifact().getVersion());

        return result.build();
    }

    private Node createOriginalNode(DependencyNode dependencyNode)
    {
        Artifact artifact = dependencyNode.getArtifact();

        Artifact result = new DefaultArtifact(
            artifact.getGroupId(),
            artifact.getArtifactId(),
            artifact.getClassifier(),
            artifact.getExtension(),
            artifact.getVersion());

        String premanagedVersion = DependencyManagerUtils.getPremanagedVersion(dependencyNode);
        if (premanagedVersion != null)
        {
            result = result.setVersion(premanagedVersion);
        }

        return nodeFactory.create(result);
    }
}
