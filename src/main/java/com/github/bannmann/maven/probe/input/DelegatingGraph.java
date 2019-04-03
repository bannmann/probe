package com.github.bannmann.maven.probe.input;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import com.github.bannmann.maven.probe.model.Graph;
import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.graph.Network;

@Slf4j
@RequiredArgsConstructor
class DelegatingGraph implements Graph
{
    @Delegate
    private final Network<Node, Edge> target;
}