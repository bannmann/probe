package com.github.bannmann.maven.probe.input;

import java.util.Set;

import lombok.EqualsAndHashCode;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

import com.google.common.collect.ImmutableSet;

/**
 * Filters all dependencies based on their scope, not distinguishing between direct and transitive dependencies.
 */
@EqualsAndHashCode
public final class StrictScopeDependencySelector implements DependencySelector
{
    private final Set<String> included;
    private final Set<String> excluded;

    /**
     * @param included The set of scopes to include, or {@code null} to include any scope.
     * @param included The set of scopes to exclude, or {@code null} to exclude no scope.
     */
    StrictScopeDependencySelector(Iterable<String> included, Iterable<String> excluded)
    {
        this.included = copy(included);
        this.excluded = copy(excluded);
    }

    Set<String> copy(Iterable<String> iterable)
    {
        if (iterable != null)
        {
            return ImmutableSet.copyOf(iterable);
        }
        else
        {
            return null;
        }
    }

    public boolean selectDependency(Dependency dependency)
    {
        return isIncluded(dependency) && !isExcluded(dependency);
    }

    private boolean isIncluded(Dependency dependency)
    {
        return included == null || included.contains(dependency.getScope());
    }

    private boolean isExcluded(Dependency dependency)
    {
        return excluded != null && excluded.contains(dependency.getScope());
    }

    public DependencySelector deriveChildSelector(DependencyCollectionContext context)
    {
        return this;
    }
}
