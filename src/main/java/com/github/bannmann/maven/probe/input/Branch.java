package com.github.bannmann.maven.probe.input;

import java.util.Objects;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;

@Value
@Builder(toBuilder = true)
final class Branch
{
    @NonNull Edge edge;

    @NonNull Node node;

    public boolean matches(Branch other)
    {
        return edge.matches(other.edge) && Objects.equals(node, other.node);
    }

    public boolean differs(Branch other)
    {
        return !matches(other);
    }

    public void verifyActive()
    {
        if (edge.getType() != Edge.Type.ACTIVE)
        {
            throw new IllegalArgumentException("Branch must be " + Edge.Type.ACTIVE);
        }
    }
}
