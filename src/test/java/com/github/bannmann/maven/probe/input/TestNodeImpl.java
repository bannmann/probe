package com.github.bannmann.maven.probe.input;

import org.testng.annotations.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public final class TestNodeImpl
{
    @Test
    public void test()
    {
        EqualsVerifier.forClass(NodeImpl.class).verify();
    }
}
