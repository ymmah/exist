package org.exist.util;

import se.softhouse.jargo.Argument;
import se.softhouse.jargo.ParsedArguments;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility functions for working with Jargo
 */
public class ArgumentUtil {

    /**
     * Get the value of an optional argument
     *
     * @param parsedArguments The arguments which have been parsed
     * @param argument The argument that we are looking for
     *
     * @return Some value or {@link Optional#empty()} if the
     *     argument was not supplied
     */
    public static <T> Optional<T> getOpt(final ParsedArguments parsedArguments, final Argument<T> argument) {
        if(parsedArguments.wasGiven(argument)) {
            return Optional.of(parsedArguments.get(argument));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the values of an optional argument
     *
     * @param parsedArguments The arguments which have been parsed
     * @param argument The argument that we are looking for
     *
     * @return A list of the provided argument values, or
     *     an empty list if the argument was not supplied
     */
    public static <T> List<T> getListOpt(final ParsedArguments parsedArguments, final Argument<List<T>> argument) {
        return getOpt(parsedArguments, argument)
                .orElseGet(() -> Collections.emptyList());
    }

    /**
     * Get the value of an optional file argument
     *
     * @param parsedArguments The arguments which have been parsed
     * @param argument The argument that we are looking for
     *
     * @return Some {@link java.nio.file.Path} or
     *     {@link Optional#empty()} if the argument was not supplied
     */
    public static Optional<Path> getPathOpt(final ParsedArguments parsedArguments, final Argument<File> argument) {
        return getOpt(parsedArguments, argument).map(File::toPath);
    }

    public static List<Path> getPathsOpt(final ParsedArguments parsedArguments, final Argument<List<File>> argument) {
        try(final Stream<File> files = getListOpt(parsedArguments, argument).stream()) {
            return files.map(File::toPath)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get the value of an option argument
     *
     * @param parsedArguments The arguments which have been parsed
     * @param argument The option argument that we are looking for
     *
     * @return true if the option was set, false otherwise
     */
    public static boolean getBool(final ParsedArguments parsedArguments, final Argument<Boolean> argument) {
        return getOpt(parsedArguments, argument)
                .flatMap(Optional::ofNullable)
                .orElse(false);
    }
}
