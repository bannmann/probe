package com.github.bannmann.maven.probe.output;

import static com.github.bannmann.maven.probe.model.Edge.Type.ACTIVE;

import java.util.EnumMap;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Node;

@Value
@Builder
final class Dependency
{
    @Getter(value = AccessLevel.NONE)
    EnumMap<Edge.Type, Edge> edges;

    Node target;

    Graph graph;

    public Optional<Edge> getEdge(Edge.Type type)
    {
        return (edges == null) ? Optional.empty() : Optional.ofNullable(edges.get(type));
    }

    public Optional<Edge> getSingleEdge()
    {
        if (edges != null && edges.size() == 1)
        {
            return Optional.of(edges.values().iterator().next());
        }
        return Optional.empty();
    }

    /**
     * Gets the primary edge, which is either the sole edge of this dependency or the one with {@link Edge.Type#ACTIVE}.
     * If no such edge exists, returns an empty <code>Optional</code>.
     *
     * @return never <code>null</code>.
     */
    public Optional<Edge> getPrimaryEdge()
    {
        Optional<Edge> activeEdgeOptional = getEdge(ACTIVE);
        return MoreOptionals.firstPresent(activeEdgeOptional, getSingleEdge());
    }

    public boolean hasEdge(Edge.Type type)
    {
        return getEdge(type).isPresent();
    }

}
