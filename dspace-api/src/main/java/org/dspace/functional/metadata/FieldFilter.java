package org.dspace.functional.metadata;

import org.dspace.content.MetadataValue;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 01 Mar 2017
 */
public interface FieldFilter extends Predicate<MetadataValue> {
}
