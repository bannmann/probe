package com.github.bannmann.maven.probe.output;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;

import com.github.bannmann.maven.probe.model.Attributes;
import com.github.bannmann.maven.probe.model.Node;

public final class TreeRenderer
{
    public void render(Node rootNode, Consumer<String> lineConsumer)
    {
        renderLine(lineConsumer, rootNode, null, true);
    }

    private void renderLine(Consumer<String> lineConsumer, Node node, String prefix, boolean isTail)
    {
        String selfPrefix = "";
        String childPrefix = "";
        if (prefix != null)
        {
            // TODO tree=graphical|classic
            selfPrefix = prefix + (isTail ? "└── " : "├── ");
            childPrefix = prefix + (isTail ? "    " : "│   ");
        }

        lineConsumer.accept(selfPrefix + renderNode(node));

        List<Node> children = node.getChildren();

        // Print all except last child
        for (int i = 0; i < children.size() - 1; i++)
        {
            renderLine(lineConsumer, children.get(i), childPrefix, false);
        }

        // Print last child
        if (!children.isEmpty())
        {
            renderLine(lineConsumer, children.get(children.size() - 1), childPrefix, true);
        }
    }

    private String renderNode(Node node)
    {
        StringBuilder result = new StringBuilder();
        result.append(node.getArtifact());

        getScopeRemark(node).ifPresent(result::append);
        getOptionalityRemark(node).ifPresent(result::append);

        getAttributeManagedRemark("version", Attributes::getVersion, node).ifPresent(result::append);
        getAttributeManagedRemark("scope", Attributes::getScope, node).ifPresent(result::append);
        getAttributeManagedRemark("optional", Attributes::getOptional, node).ifPresent(result::append);

        getWinnerRemark(node).ifPresent(result::append);

        return result.toString();
    }

    private Optional<String> getScopeRemark(Node node)
    {
        Optional<String> result = Optional.empty();

        String scope = node.getAttributes().getScope();
        if (!scope.equals("compile"))
        {
            result = Optional.of(MessageFormat.format(" [{0}]", scope));
        }

        return result;
    }

    private Optional<String> getOptionalityRemark(Node node)
    {
        Optional<String> result = Optional.empty();

        if (node.getAttributes().getOptional())
        {
            result = Optional.of(" <optional>");
        }

        return result;
    }

    private Optional<String> getAttributeManagedRemark(String label, Function<Attributes, Object> accessor, Node node)
    {
        Optional<String> result = Optional.empty();

        Object premanagedValue = accessor.apply(node.getPremanagedAttributes());
        Object managedValue = accessor.apply(node.getAttributes());

        // This is not simply a null check to guard equals(): premanagedValue is null when nothing was managed.
        if (premanagedValue != null && !premanagedValue.equals(managedValue))
        {
            result = Optional.of(MessageFormat.format(" ({0} managed from {1})", label, premanagedValue));
        }

        return result;
    }

    private Optional<String> getWinnerRemark(Node node)
    {
        String result = null;

        Artifact winner = node.getWinningArtifact();
        if (winner != null)
        {
            result = MessageFormat.format(" '{'conflicts with {0}'}'", getShortArtifactId(node.getArtifact(), winner));
        }

        return Optional.ofNullable(result);
    }

    private String getShortArtifactId(Artifact base, Artifact destination)
    {
        String result = destination.toString();
        if (ArtifactIdUtils.toVersionlessId(base).equals(ArtifactIdUtils.toVersionlessId(destination)))
        {
            result = destination.getVersion();
        }
        return result;
    }
}
