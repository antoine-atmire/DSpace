package org.dspace.functional.metadata;

import org.apache.commons.lang.ObjectUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 01 Mar 2017
 */
public class FilterByFieldString implements FieldFilter {

    private final FieldString fieldString;

    public FilterByFieldString(FieldString fieldString) {
        this.fieldString = fieldString;
    }

    @Override
    public boolean test(MetadataValue metadataValue) {
        MetadataField metadataField = metadataValue.getMetadataField();
        return match(metadataField.getMetadataSchema().getName(), fieldString.getSchema())
                && match(metadataField.getElement(), fieldString.getElement())
                && match(metadataField.getQualifier(), fieldString.getQualifier())
                && match(metadataValue.getLanguage(), fieldString.getLanguage())
                && match(metadataValue.getAuthority(), fieldString.getAuthority())
                && match(metadataValue.getConfidence(), fieldString.getConfidence());
    }

    private boolean match(String given, String filter) {
        return Item.ANY.equals(filter) || ObjectUtils.equals(given, filter);
    }

    boolean match(int given, int filter) {
        return Choices.CF_UNSET == filter || given == filter;
    }

}
