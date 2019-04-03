package com.github.bannmann.maven.probe.input;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import com.github.bannmann.maven.probe.model.Edge;

@Value
@Builder(toBuilder = true)
@ToString(doNotUseGetters = true)
final class EdgeImpl implements Edge
{
    public static class EdgeImplBuilder
    {
        // Declaring this class prevents lombok from making the builder class package-protected (like EdgeImpl)
    }

    public static EdgeImplBuilder toBuilder(Edge edge)
    {
        return ((EdgeImpl) edge).toBuilder();
    }

    @Getter
    @NonNull
    private final Type type;

    @ToString.Exclude
    private Edge active;

    private String scope;

    private Boolean optional;

    private String version;

    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    public boolean equals(Object other)
    {
        return this == other;
    }

    @Override
    public Optional<Edge> getActive()
    {
        return Optional.ofNullable(active);
    }

    @Override
    public Optional<String> getScope()
    {
        return Optional.ofNullable(scope);
    }

    @Override
    public Optional<Boolean> getOptional()
    {
        return Optional.ofNullable(optional);
    }

    @Override
    public Optional<String> getVersion()
    {
        return Optional.ofNullable(version);
    }
}
