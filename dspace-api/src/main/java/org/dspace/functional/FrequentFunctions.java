package org.dspace.functional;

import org.dspace.content.MetadataValue;
import org.dspace.functional.metadata.FieldString;
import org.dspace.functional.metadata.FilterByFieldString;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 01 Mar 2017
 */
public class FrequentFunctions {

    public static Function<? super MetadataValue, ? extends String> getValues() {
        return MetadataValue::getValue;
    }

    public static Predicate<? super MetadataValue> filterByField(FieldString field) {
        return new FilterByFieldString(field);
    }

    public static Predicate<? super MetadataValue> filterByLanguage(String language) {
        return filterBy(language, MetadataValue::getLanguage);
    }

    public static <O, T> Predicate<? super O> filterBy(T value, Function<O, T> function) {
        return function.andThen(value::equals)::apply;
        // return someValue -> Objects.equals(value, function.apply(someValue));
    }

    public static <T> Predicate<T> filterBy(T value) {
        return value::equals;
    }

    public static Comparator<? super MetadataValue> compareByValue() {
        return compareBy(MetadataValue::getValue);
    }

    public static <O> Comparator<? super O> compareBy(Function<O, Comparable> function) {
        return compareBy(Stream.of(function));
    }

    public static <O> Comparator<? super O> compareBy(Function<O, Comparable> function1, Function<O, Comparable> function2) {
        return compareBy(Stream.of(function1, function2));
    }

    public static <O> Comparator<O> compareBy(Stream<Function<O, Comparable>> stream) {
        //noinspection ComparatorMethodParameterNotUsed
        return stream.reduce(
                (a, b) -> 0,
                (comparator, function) -> comparator.thenComparing(Comparator
                        .comparing(function, Comparator.nullsLast(Comparator.naturalOrder()))
                ),
                Comparator::thenComparing
        );

        // this implementation seems better to me, but I don't know why it does not compile
        // return stream.map(f -> Comparator.comparing(f, Comparator.nullsLast(Comparator.naturalOrder())))
        // .reduce(Comparator::thenComparing)
        // .orElse((a, b) -> 0)
        // ;
    }

    public static Function<MetadataValue, String> fieldString() {
        return fieldString(Function.identity());
    }

    public static Function<MetadataValue, String> fieldString(Function<FieldString, FieldString> transform) {
        return ((Function<MetadataValue, FieldString>) FieldString::new)
                .andThen(transform)
                .andThen(FieldString::toString);
        // return m -> transform.apply(new FieldString(m)).toString();
    }

    public static Function<MetadataValue, Comparable> compareFieldString() {
        return fieldString().andThen(Function.identity());
    }

    public static Function<MetadataValue, Comparable> compareSignature() {
        return fieldString(FieldString::signatureOnly).andThen(Function.identity());
    }
}
