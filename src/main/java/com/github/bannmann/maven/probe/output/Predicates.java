package com.github.bannmann.maven.probe.output;

import java.util.function.Predicate;

import lombok.experimental.UtilityClass;

@UtilityClass
class Predicates
{
    public static Predicate<Object> notEqual(Object other)
    {
        return Predicate.isEqual(other).negate();
    }
}
