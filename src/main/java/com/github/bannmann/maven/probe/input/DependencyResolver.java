package com.github.bannmann.maven.probe.input;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;

@Slf4j
final class DependencyResolver
{
    @Inject
    private RepositorySessionFactory sessionFactory;

    @Inject
    private ScopeDependencySelector scopeDependencySelector;

    @Inject
    private ProjectDependenciesResolver projectDependenciesResolver;

    @Inject
    private ResolutionState resolutionState;

    public DependencyResolutionResult resolve(MavenProject project)
    {
        DefaultDependencyResolutionRequest request = new DefaultDependencyResolutionRequest();
        request.setMavenProject(project);

        DefaultRepositorySystemSession session = sessionFactory.create();
        session.setDependencySelector(scopeDependencySelector);
        request.setRepositorySession(session);

        try
        {
            return projectDependenciesResolver.resolve(request);
        }
        catch (DependencyResolutionException e)
        {
            DependencyResolutionResult result = e.getResult();

            result.getCollectionErrors().stream().map(Throwable::getMessage).forEach(log::warn);

            List<Dependency> unresolved = result.getUnresolvedDependencies();
            if (!unresolved.isEmpty())
            {
                String prefix = "\n - ";
                log.warn(
                    "Could not resolve dependencies for the following artifacts:{}",
                    unresolved.stream()
                        .map(Dependency::getArtifact)
                        .map(this::getArtifactIdAndParent)
                        .collect(Collectors.joining(prefix, prefix, "")));
            }

            return result;
        }
    }

    private String getArtifactIdAndParent(Artifact artifact)
    {
        return ArtifactIdUtils.toId(artifact) +
            ", referenced by " +
            resolutionState.getGraph()
                .predecessors(artifact)
                .stream()
                .map(ArtifactIdUtils::toId)
                .collect(Collectors.joining(", "));
    }

}
