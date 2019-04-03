package com.github.bannmann.maven.probe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.aether.collection.DependencyCollectionException;

import com.github.bannmann.maven.probe.input.DependencyGraphBuilder;
import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.output.TextRenderer;
import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;

@Slf4j
@ProcessingStep(id = "probe", description = "Determines the project dependency graph.", requiresOnline = false)
public final class ProbeStep implements CDIMojoProcessingStep
{
    @Inject
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Inject
    @Named("skip")
    private boolean skip;

    @Inject
    @Named("outputFile")
    protected Path outputFile;

    @Override
    public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            log.info("Skipping Probe Plugin");
        }
        else
        {
            try
            {
                Graph graph = dependencyGraphBuilder.getGraph();

                if (outputFile != null)
                {
                    writeToFile(graph, outputFile);
                }
                else
                {
                    writeToConsole(graph);
                }
            }
            catch (DependencyCollectionException e)
            {
                throw new MojoFailureException("Could not collect dependencies", e);
            }
        }

    }

    private void writeToFile(Graph graph, Path file)
    {
        try (
            BufferedWriter bufferedWriter = Files.newBufferedWriter(file);
            PrintWriter printWriter = new PrintWriter(bufferedWriter))
        {
            log.info("Writing tree to {}", file.toAbsolutePath());
            new TextRenderer(graph).render(printWriter::println);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private void writeToConsole(Graph graph)
    {
        new TextRenderer(graph).render(log::info);
    }
}