package com.github.bannmann.maven.probe;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import com.itemis.maven.plugins.cdi.AbstractCDIMojo;
import com.itemis.maven.plugins.cdi.annotations.MojoProduces;

@Slf4j
@Mojo(name = "probe",
    requiresDependencyResolution = ResolutionScope.TEST,
    threadSafe = true,
    defaultPhase = LifecyclePhase.VALIDATE)
public final class ProbeMojo extends AbstractCDIMojo
{
    static
    {
        System.setProperty("org.slf4j.simpleLogger.log.org.jboss.weld", "warn");
        System.setProperty("org.slf4j.simpleLogger.log.com.itemis.maven.plugins.cdi", "warn");
    }

    /**
     * Set this to 'true' to skip probe plugin execution
     */
    @Parameter(property = "probe.skip", defaultValue = "false")
    @MojoProduces
    @Named("skip")
    private boolean skip;

    /**
     * Set this to 'true' to also include artifacts which are not part of the tree due to mediation/management, plus
     * their dependencies.
     */
    @Parameter(property = "probe.includeInactive", defaultValue = "false")
    @MojoProduces
    @Named("includeInactive")
    private boolean includeInactive;

    /**
     * Set this to 'true' to also include dependencies which are optional.
     */
    @Parameter(property = "probe.includeOptional", defaultValue = "false")
    @MojoProduces
    @Named("includeOptional")
    private boolean includeOptional;

    /**
     * Probe tree is written to this file instead of the console.
     */
    @Parameter(property = "probe.outputFile")
    private String outputFile;

    @Component
    @MojoProduces
    private ProjectDependenciesResolver projectDependenciesResolver;

    @Component
    @MojoProduces
    private RepositorySystem system;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    @MojoProduces
    private RepositorySystemSession globalSystemSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    @MojoProduces
    private List<RemoteRepository> repositories;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    @MojoProduces
    private MavenProject project;

    @MojoProduces
    @Named("outputFile")
    protected Path produceOutputFile()
    {
        Path result = null;
        if (outputFile != null)
        {
            result = Paths.get(outputFile);
        }
        return result;
    }
}
