package com.github.bannmann.maven.probe.input;

import javax.enterprise.inject.Produces;

import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;

import com.google.common.collect.ImmutableSet;

final class ScopeDependencySelectorProducer
{
    private static final ImmutableSet<String> EXCLUDED = ImmutableSet.of("test");

    @Produces
    ScopeDependencySelector produceDefault()
    {
        return new ScopeDependencySelector(null, EXCLUDED);
    }

    @Produces
    StrictScopeDependencySelector produceStrict()
    {
        return new StrictScopeDependencySelector(null, EXCLUDED);
    }
}
