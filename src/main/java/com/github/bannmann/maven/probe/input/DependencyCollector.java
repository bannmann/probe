package com.github.bannmann.maven.probe.input;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

import com.google.common.collect.ImmutableList;

final class DependencyCollector
{
    private static final DependencySelector SELECTOR = new ScopeDependencySelector(ImmutableList.of("compile"), null);

    @Inject
    private RepositorySystemSession globalSystemSession;

    @Inject
    private List<RemoteRepository> repositories;

    @Inject
    private RepositorySystem system;

    public CollectResult collect(Artifact rootArtifact) throws DependencyCollectionException
    {
        Dependency rootDependency = new Dependency(rootArtifact, "compile");

        DefaultRepositorySystemSession systemSession = new DefaultRepositorySystemSession(globalSystemSession);
        systemSession.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
        systemSession.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);
        systemSession.setDependencySelector(SELECTOR);

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(rootDependency);
        collectRequest.setRepositories(repositories);

        return system.collectDependencies(systemSession, collectRequest);
    }
}
