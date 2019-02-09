package com.github.bannmann.maven.probe.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class Attributes
{
    private final String version;
    private final String scope;
    private final Boolean optional;
}
