package com.github.bannmann.maven.probe.input;

import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

import com.github.bannmann.maven.probe.model.Node;
import com.google.common.collect.ImmutableList;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DependencyLister
{
    @RequiredArgsConstructor
    private static class RootDependencySelector implements DependencySelector
    {
        private static final DependencySelector
            CHILD_SELECTOR
            = new AndDependencySelector(new OptionalDependencySelector(),
            new ScopeDependencySelector(ImmutableList.of("compile"), null));

        private final Dependency rootDependency;

        @Override
        public boolean selectDependency(Dependency dependency)
        {
            return dependency.equals(rootDependency);
        }

        @Override
        public DependencySelector deriveChildSelector(DependencyCollectionContext context)
        {
            return CHILD_SELECTOR;
        }
    }

    private final MavenProject project;
    private final RepositorySystemSession globalSystemSession;
    private final List<RemoteRepository> repositories;
    private final RepositorySystem system;

    public Node getNode() throws DependencyCollectionException
    {
        DefaultArtifact rootArtifact = new DefaultArtifact(project.getArtifact().toString());
        Dependency rootDependency = new Dependency(rootArtifact, "compile");

        DefaultRepositorySystemSession systemSession = new DefaultRepositorySystemSession(globalSystemSession);
        systemSession.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
        systemSession.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);
        systemSession.setDependencySelector(new RootDependencySelector(rootDependency));

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(rootDependency);
        collectRequest.setRepositories(repositories);
        CollectResult collectResult = system.collectDependencies(systemSession, collectRequest);
        NodeTreeBuilder nodeTreeBuilder = new NodeTreeBuilder();
        collectResult.getRoot().accept(nodeTreeBuilder);
        return nodeTreeBuilder.getRoot();
    }
}
