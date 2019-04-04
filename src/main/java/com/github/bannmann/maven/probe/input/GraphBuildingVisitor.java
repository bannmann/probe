package com.github.bannmann.maven.probe.input;

import org.eclipse.aether.graph.DependencyVisitor;

import com.github.bannmann.maven.probe.model.Edge;
import com.github.bannmann.maven.probe.model.Node;
import com.google.common.graph.MutableNetwork;

interface GraphBuildingVisitor extends DependencyVisitor
{
    void initialize(MutableNetwork<Node, Edge> network);
}
