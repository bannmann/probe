package com.github.bannmann.maven.probe.input;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;

@Slf4j
final class RepositorySessionFactory
{
    @Inject
    private RepositorySystemSession globalSystemSession;

    @Inject
    private ResolutionState resolutionState;

    public DefaultRepositorySystemSession create()
    {
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession(globalSystemSession);
        setVerbose(session);
        addDependencyGraphTransformer(session);
        return session;
    }

    private void setVerbose(DefaultRepositorySystemSession session)
    {
        session.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
        session.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);
    }

    private void addDependencyGraphTransformer(DefaultRepositorySystemSession session)
    {
        DependencyGraphTransformer originalTransformer = session.getDependencyGraphTransformer();

        session.setDependencyGraphTransformer((node, context) -> {
            resolutionState.scan(node);
            return originalTransformer.transformGraph(node, context);
        });
    }
}
