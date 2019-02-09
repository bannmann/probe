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

import com.github.bannmann.maven.probe.input.DependencyLister;
import com.github.bannmann.maven.probe.model.Node;
import com.github.bannmann.maven.probe.output.TreeRenderer;
import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;

@Slf4j
@ProcessingStep(id = "probe", description = "Determines the project dependency graph.", requiresOnline = false)
public class ProbeStep implements CDIMojoProcessingStep
{
    @Inject
    private DependencyLister dependencyLister;

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
                Node root = dependencyLister.getNode();

                if (outputFile != null)
                {
                    writeToFile(root, outputFile);
                }
                else
                {
                    writeToConsole(root);
                }
            }
            catch (DependencyCollectionException e)
            {
                throw new MojoFailureException("Could not collect dependencies", e);
            }
        }

    }

    private void writeToFile(Node root, Path file)
    {
        try (
            BufferedWriter bufferedWriter = Files.newBufferedWriter(file);
            PrintWriter printWriter = new PrintWriter(bufferedWriter))
        {
            log.info("Writing tree to {}", file.toAbsolutePath());
            new TreeRenderer().render(root, printWriter::println);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private void writeToConsole(Node root)
    {
        new TreeRenderer().render(root, log::info);
    }
}