package com.github.bannmann.maven.probe.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.eclipse.aether.artifact.Artifact;

@Data
public final class Node
{
    private final List<Node> children = new ArrayList<>();

    private Artifact artifact;
    private Artifact unmediatedArtifact;
    private Attributes attributes;
    private Attributes premanagedAttributes;

    public void add(Node child)
    {
        children.add(child);
    }
}
