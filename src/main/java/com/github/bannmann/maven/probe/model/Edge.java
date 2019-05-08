package com.github.bannmann.maven.probe.model;

import java.util.Objects;
import java.util.Optional;

public interface Edge
{
    enum Type
    {
        ACTIVE, MANAGED, MEDIATED, ORIGINAL, INACTIVE,

        /**
         * The synthetic inbound edge of the root node.
         */
        ROOT
    }

    Optional<Edge> getActive();

    Optional<String> getScope();

    Optional<Boolean> getOptional();

    Optional<String> getVersion();

    Type getType();

    default boolean matches(Edge other)
    {
        return Objects.equals(getScope(), other.getScope()) &&
            Objects.equals(getOptional(), other.getOptional()) &&
            Objects.equals(getVersion(), other.getVersion());
    }

    default boolean differs(Edge other)
    {
        return !matches(other);
    }
}
