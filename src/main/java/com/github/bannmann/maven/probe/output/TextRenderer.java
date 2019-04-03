package com.github.bannmann.maven.probe.output;

import static com.github.bannmann.maven.probe.output.Functions.applyMessageFormat;
import static com.github.bannmann.maven.probe.output.Predicates.notEqual;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Edge;

public final class TextRenderer
{
    private final DependencyGraph dependencyGraph;

    public TextRenderer(Graph graph)
    {
        this.dependencyGraph = new DependencyGraph(graph);
    }

    public void render(Consumer<String> lineConsumer)
    {
        Dependency dependency = dependencyGraph.getRoot();
        renderLine(lineConsumer, dependency, null, true);
    }

    private void renderLine(Consumer<String> lineConsumer, Dependency dependency, String prefix, boolean isTail)
    {
        String selfPrefix = "";
        String childPrefix = "";
        if (prefix != null)
        {
            selfPrefix = prefix + (isTail ? "└── " : "├── ");
            childPrefix = prefix + (isTail ? "    " : "│   ");
        }

        lineConsumer.accept(selfPrefix + renderNode(dependency));

        List<Dependency> dependencyList = dependencyGraph.getDependencies(dependency.getTarget());
        for (ContextIterables.Iteration<Dependency> iteration : ContextIterables.create(dependencyList))
        {
            renderLine(lineConsumer, iteration.getElement(), childPrefix, iteration.isLast());
        }
    }

    private String renderNode(Dependency dependency)
    {
        StringBuilder result = new StringBuilder();

        result.append(getNodeLabel(dependency));

        dependency.getEdge(Edge.Type.ACTIVE).ifPresent(activeEdge -> {
            getScopeLabel(activeEdge).ifPresent(result::append);
            getOptionalityLabel(activeEdge).ifPresent(result::append);
            getManagementLabel("version", Edge::getVersion, dependency).ifPresent(result::append);
            getManagementLabel("scope", Edge::getScope, dependency).ifPresent(result::append);
            getManagementLabel("optional", Edge::getOptional, dependency).ifPresent(result::append);
            getMediationLabel(dependency).ifPresent(result::append);
        });

        return result.toString();
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
            Edge.Type.MANAGED,
            Edge.Type.ORIGINAL,
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
        return getSecondIfDifferent(dependency,
            Edge.Type.MEDIATED,
            Edge.Type.ORIGINAL,
            Edge::getVersion,
            " '{'mediated from {0}'}'");
    }
}
