package com.github.bannmann.maven.probe.input;

import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@Slf4j
final class DependencyCollector
{
    @Inject
    private RepositorySessionFactory sessionFactory;

    @Inject
    private StrictScopeDependencySelector strictScopeDependencySelector;

    @Inject
    private RepositorySystem repositorySystem;

    @Inject
    private List<RemoteRepository> repositories;

    public CollectResult collect(Artifact rootArtifact) throws DependencyCollectionException
    {
        Dependency rootDependency = new Dependency(rootArtifact, "compile");

        DefaultRepositorySystemSession session = sessionFactory.create();
        session.setDependencySelector(strictScopeDependencySelector);

        CollectRequest collectRequest = createRequest(rootDependency);

        return repositorySystem.collectDependencies(session, collectRequest);
    }

    private CollectRequest createRequest(Dependency rootDependency)
    {
        CollectRequest request = new CollectRequest();
        request.setRoot(rootDependency);
        request.setRepositories(repositories);
        return request;
    }
}
