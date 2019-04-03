package com.github.bannmann.maven.probe.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;

import com.github.bannmann.maven.probe.model.Node;

/**
 * Wraps an {@link Artifact} in a way that is safe for usage in collections and graphs.
 */
@Value
@ToString(includeFieldNames = false)
final class NodeImpl implements Node
{
    /**
     * 'Artifact' does not override equals(), so this field is transient and therefore ignored by Node.equals().
     */
    @Getter
    @NonNull
    @ToString.Exclude
    private final transient Artifact artifact;

    private final String coordinates;

    public NodeImpl(@NonNull Artifact artifact)
    {
        this.artifact = artifact;
        this.coordinates = ArtifactIdUtils.toId(artifact);
    }
}
