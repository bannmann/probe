package com.github.bannmann.maven.probe.input;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import com.github.bannmann.maven.probe.util.DequeStack;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

@Slf4j
@Singleton
public class ResolutionState
{
    @RequiredArgsConstructor(onConstructor = @__(@Inject))
    private final class Visitor implements DependencyVisitor
    {
        private final DequeStack<Artifact> parents = new DequeStack<>();

        public boolean visitEnter(DependencyNode dependencyNode)
        {
            Artifact artifact = dependencyNode.getArtifact();
            if (parents.contains(artifact))
            {
                return false;
            }

            graph.addNode(artifact);

            parents.current().ifPresent(parent -> graph.putEdge(parent, artifact));

            parents.enter(artifact);

            return true;
        }

        public boolean visitLeave(DependencyNode dependencyNode)
        {
            Artifact artifact = dependencyNode.getArtifact();
            parents.tryLeave(artifact);
            return true;
        }
    }

    private final MutableGraph<Artifact> graph = GraphBuilder.directed().build();

    public void scan(DependencyNode dependencyNode)
    {
        dependencyNode.accept(new Visitor());
    }

    public Graph<Artifact> getGraph()
    {
        return graph;
    }
}
