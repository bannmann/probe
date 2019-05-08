package com.github.bannmann.maven.probe.output;

import static com.github.bannmann.maven.probe.model.Edge.Type.ACTIVE;
import static com.github.bannmann.maven.probe.model.Edge.Type.INACTIVE;
import static com.github.bannmann.maven.probe.model.Edge.Type.MANAGED;
import static com.github.bannmann.maven.probe.model.Edge.Type.MEDIATED;
import static com.github.bannmann.maven.probe.model.Edge.Type.ORIGINAL;
import static com.github.bannmann.maven.probe.output.Functions.applyMessageFormat;
import static com.github.bannmann.maven.probe.output.Predicates.notEqual;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Node;
import com.github.bannmann.maven.probe.util.DequeStack;

public final class TextRenderer
{
    @Inject
    private Provider<DependencyGraph> dependencyGraphProvider;

    private final DequeStack renderedParents = new DequeStack();

    public void render(Graph graph, Consumer<String> lineConsumer)
    {
        DependencyGraph dependencyGraph = dependencyGraphProvider.get();
        dependencyGraph.initialize(graph);
        Dependency dependency = dependencyGraph.getRoot();
        renderLine(dependencyGraph, dependency, null, true, lineConsumer, false);
    }

    private void renderLine(
        DependencyGraph dependencyGraph,
        Dependency dependency,
        String prefix,
        boolean isTail,
        Consumer<String> lineConsumer,
        boolean forceInactive)
    {
        String selfPrefix = "";
        String childPrefix = "";

        if (prefix != null)
        {
            if (!isActive(dependency) || forceInactive)
            {
                selfPrefix = prefix + (isTail ? "└╶╶ " : "├╶╶ ");

                // Ensure descendants are rendered inactive here even if they are already active elsewhere in the tree
                forceInactive = true;
            }
            else
            {
                selfPrefix = prefix + (isTail ? "└── " : "├── ");
            }
            childPrefix = prefix + (isTail ? "    " : "│   ");
        }

        Node target = dependency.getTarget();

        List<Dependency> dependencyList = dependencyGraph.getDependencies(target);
        String loopHint = "";
        if (renderedParents.contains(target))
        {
            dependencyList = Collections.emptyList();
            loopHint = " // dependencies omitted to avoid loop";
        }

        lineConsumer.accept(selfPrefix + renderNode(dependency, forceInactive) + loopHint);

        renderedParents.enter(target);

        for (ContextIterables.Iteration<Dependency> iteration : ContextIterables.create(dependencyList))
        {
            renderLine(dependencyGraph,
                iteration.getElement(),
                childPrefix,
                iteration.isLast(),
                lineConsumer,
                forceInactive);
        }

        renderedParents.leave();
    }

    private String renderNode(Dependency dependency, boolean forceInactive)
    {
        StringBuilder result = new StringBuilder();

        result.append(getNodeLabel(dependency));

        Optional<Edge> activeEdgeOptional = dependency.getEdge(ACTIVE);
        Optional<Edge> primaryEdgeOptional = dependency.getPrimaryEdge();

        primaryEdgeOptional.ifPresent(edge -> {
            getScopeLabel(edge).ifPresent(result::append);
            getOptionalityLabel(edge).ifPresent(result::append);
        });

        activeEdgeOptional.ifPresent(activeEdge -> {
            getManagementLabel("version", Edge::getVersion, dependency).ifPresent(result::append);
            getManagementLabel("scope", Edge::getScope, dependency).ifPresent(result::append);
            getManagementLabel("optional", Edge::getOptional, dependency).ifPresent(result::append);
            getMediationLabel(dependency).ifPresent(result::append);
        });

        MoreOptionals.firstPresent(getInactiveEdgeTypeHint(dependency, ORIGINAL),
            getInactiveEdgeTypeHint(dependency, INACTIVE),
            getForcedInactiveHint(forceInactive)).ifPresent(result::append);

        return result.toString();
    }

    private Optional<String> getForcedInactiveHint(boolean forceInactive)
    {
        return Optional.of(forceInactive).filter(isTrue()).map(b -> " ~inactive~");
    }

    private Predicate<Boolean> isTrue()
    {
        return force -> force;
    }

    private String getNodeLabel(Dependency dependency)
    {
        return dependency.getTarget().getArtifact().toString();
    }

    private Optional<String> getScopeLabel(Edge edge)
    {
        return edge.getScope().filter(notEqual("compile")).map(applyMessageFormat(" [{0}]"));
    }

    private Optional<String> getOptionalityLabel(Edge edge)
    {
        return edge.getOptional().filter(Boolean::booleanValue).map(isTrue -> " <optional>");
    }

    private Optional<String> getManagementLabel(
        String attribute, Function<Edge, Optional<?>> getAttribute, Dependency dependency)
    {
        return getSecondIfDifferent(dependency,
            MANAGED,
            ORIGINAL,
            getAttribute,
            " (" + attribute + " managed from {0})");
    }

    private Optional<String> getSecondIfDifferent(
        Dependency dependency,
        Edge.Type first,
        Edge.Type second,
        Function<Edge, Optional<?>> getAttribute,
        String pattern)
    {
        return Stream.of(first, second)
            .map(dependency::getEdge)
            .flatMap(MoreOptionals::mapToValue)
            .map(getAttribute)
            .flatMap(MoreOptionals::mapToValue)
            .collect(collectSecondIfDifferent())
            .map(applyMessageFormat(pattern));
    }

    private <T> Collector<T, ?, Optional<T>> collectSecondIfDifferent()
    {
        return Collectors.collectingAndThen(Collectors.toList(), this::getSecondIfDifferent);
    }

    private <T> Optional<T> getSecondIfDifferent(List<T> list)
    {
        return (list.size() == 2) ? Optional.of(list.get(1)).filter(notEqual(list.get(0))) : Optional.empty();
    }

    private Optional<String> getMediationLabel(Dependency dependency)
    {
        return getSecondIfDifferent(dependency, MEDIATED, ORIGINAL, Edge::getVersion, " '{'mediated from {0}'}'");
    }

    private Optional<String> getInactiveEdgeTypeHint(Dependency dependency, Edge.Type type)
    {
        if (isActive(dependency))
        {
            return Optional.empty();
        }

        return dependency.getEdge(type)
            .map(edge -> edge.getType().toString().toLowerCase())
            .map(applyMessageFormat(" ~{0}~"));
    }

    private boolean isActive(Dependency dependency)
    {
        return dependency.hasEdge(ACTIVE);
    }
}
