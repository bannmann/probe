package com.github.bannmann.maven.probe.model;

import org.eclipse.aether.artifact.Artifact;

public interface Node
{
    String getCoordinates();

    Artifact getArtifact();
}
