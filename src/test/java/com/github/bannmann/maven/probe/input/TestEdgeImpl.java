package com.github.bannmann.maven.probe.input;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.bannmann.maven.probe.model.Edge;

public final class TestEdgeImpl
{
    private static final EdgeImpl ACTIVE = EdgeImpl.builder().type(Edge.Type.ACTIVE).build();
    private static final EdgeImpl TWIN = EdgeImpl.builder().type(Edge.Type.ACTIVE).build();
    private static final EdgeImpl DERIVED = ACTIVE.toBuilder().build();

    @Test(dataProvider = "relatedPairs")
    public void testInstancesAreUnique(String label, Edge a, Edge b)
    {
        assertThat(a).isNotEqualTo(b);
    }

    @DataProvider
    public Object[][] relatedPairs()
    {
        return new Object[][]{
            new Object[]{ "ACTIVE, TWIN", ACTIVE, TWIN },
            new Object[]{ "ACTIVE, DERIVED", ACTIVE, DERIVED }
        };
    }
}
