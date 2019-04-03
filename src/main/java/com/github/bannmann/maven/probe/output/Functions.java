package com.github.bannmann.maven.probe.output;

import java.text.MessageFormat;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
class Functions
{
    public static Function<Object, String> applyMessageFormat(String pattern)
    {
        return o -> MessageFormat.format(pattern, o);
    }
}
