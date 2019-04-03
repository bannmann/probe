package com.github.bannmann.maven.probe.output;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public final class TestContextIterables
{
    @Test
    public void testEmpty() {
        List<String> target = Collections.emptyList();
        Iterable<ContextIterables.Iteration<String>> subject = ContextIterables.create(target);

        assertThat(subject).isEmpty();
    }
    @Test
    public void test1() {
        List<String> target = Collections.singletonList("Yay");
        Iterable<ContextIterables.Iteration<String>> subject = ContextIterables.create(target);

        assertThat(subject).hasSize(1);

        ContextIterables.Iteration<String> first = subject.iterator().next();
        assertThat(first.isFirst()).isTrue();
        assertThat(first.isLast()).isTrue();
    }

    @Test
    public void test2() {
        List<String> target = ImmutableList.of("A", "B");
        Iterable<ContextIterables.Iteration<String>> subject = ContextIterables.create(target);

        assertThat(subject).hasSize(2);

        Iterator<ContextIterables.Iteration<String>> iterator = subject.iterator();

        ContextIterables.Iteration<String> one = iterator.next();
        assertThat(one.isFirst()).isTrue();
        assertThat(one.isLast()).isFalse();

        ContextIterables.Iteration<String> two = iterator.next();
        assertThat(two.isFirst()).isFalse();
        assertThat(two.isLast()).isTrue();
    }

    @Test
    public void test3() {
        List<String> target = ImmutableList.of("A", "B", "C");
        Iterable<ContextIterables.Iteration<String>> subject = ContextIterables.create(target);

        assertThat(subject).hasSize(3);

        Iterator<ContextIterables.Iteration<String>> iterator = subject.iterator();

        ContextIterables.Iteration<String> one = iterator.next();
        assertThat(one.isFirst()).isTrue();
        assertThat(one.isLast()).isFalse();

        ContextIterables.Iteration<String> two = iterator.next();
        assertThat(two.isFirst()).isFalse();
        assertThat(two.isLast()).isFalse();

        ContextIterables.Iteration<String> three = iterator.next();
        assertThat(three.isFirst()).isFalse();
        assertThat(three.isLast()).isTrue();
    }
}
