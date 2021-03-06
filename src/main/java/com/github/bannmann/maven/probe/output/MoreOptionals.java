package com.github.bannmann.maven.probe.output;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
class MoreOptionals
{
    public static <T> Stream<T> mapToValue(Optional<T> optional)
    {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}
