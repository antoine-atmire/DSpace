package org.dspace.functional.metadata;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 01 Mar 2017
 *
 * FieldString = schema.element.qualifier[language]::authority::confidence
 * qualifier is optional
 * [language] is optional
 * authority is optional
 * confidence: not present if no authority, otherwise optional
 */
public class FieldString {

    private static final String SIGNATURE_SEPARATOR = ".";
    private static final String AUTHORITY_SEPARATOR = "::";

    private final String schema;
    private final String element;
    private final String qualifier;
    private final String language;
    private final String authority;
    private final int confidence;

    public FieldString(String schema, String element, String qualifier, String language, String authority, int confidence) {
        this.schema = schema;
        this.element = element;
        this.qualifier = qualifier;
        this.language = language;
        this.authority = authority;
        this.confidence = confidence;
    }

    public FieldString(MetadataValue metadataValue) {
        this(
                metadataValue.getMetadataField().getMetadataSchema().getName(),
                metadataValue.getMetadataField().getElement(),
                metadataValue.getMetadataField().getQualifier(),
                metadataValue.getLanguage(),
                metadataValue.getAuthority(),
                metadataValue.getConfidence()
        );
    }

    public FieldString(String string) {
        String schema = null, element = null, qualifier = null, language = null, authority = null;
        int confidence = Choices.CF_UNSET;


        String[] tokens = toTokens(string);

        schema = tokens[0];
        element = tokens[1];

        String rest;
        if (tokens.length == 3) {
            rest = tokens[2];
        } else {
            rest = tokens[1];
        }

        String[] authoritySplit = StringUtils.split(rest, AUTHORITY_SEPARATOR);

        rest = authoritySplit[0];
        if (rest.contains("[")) {
            int open = rest.indexOf('[');
            int close = rest.indexOf(']');
            qualifier = rest.substring(0, open);
            language = rest.substring(open + 1, close);
        } else {
            qualifier = rest;
        }

        if (authoritySplit.length > 1) {
            authority = authoritySplit[1];
        }
        if (authoritySplit.length > 2) {
            try {
                confidence = Integer.parseInt(authoritySplit[2]);
            } catch (NumberFormatException n) {
                confidence = Choices.CF_UNSET;
            }
        }

        if (tokens.length == 2) {
            element = qualifier;
            qualifier = null;
        }


        this.schema = schema;
        this.element = element;
        this.qualifier = qualifier;
        this.language = language;
        this.authority = authority;
        this.confidence = confidence;
    }

    private String[] toTokens(String string) {
        String[] tokens;
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException("blank FieldString");
        } else {
            tokens = StringUtils.split(string, SIGNATURE_SEPARATOR);
        }
        if (tokens.length < 2) {
            throw new IllegalArgumentException(string + " = invalid FieldString: too few tokens");
        } else if (tokens.length >= 4) {
            throw new IllegalArgumentException(string + " = invalid FieldString: too many tokens");
        }
        return tokens;
    }

    public String getSchema() {
        return schema;
    }

    public String getElement() {
        return element;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getLanguage() {
        return language;
    }

    public String getAuthority() {
        return authority;
    }

    public int getConfidence() {
        return confidence;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(schema).append(SIGNATURE_SEPARATOR).append(element);
        if (StringUtils.isNotBlank(qualifier)) {
            builder.append(SIGNATURE_SEPARATOR).append(qualifier);
        }
        if (StringUtils.isNotBlank(language)) {
            builder.append('[').append(language).append(']');
        }
        if (StringUtils.isNotBlank(authority)) {
            builder.append(AUTHORITY_SEPARATOR).append(authority);
            builder.append(AUTHORITY_SEPARATOR).append(confidence);
        }
        return builder.toString();
    }

    public FieldString withSchema(String schema) {
        return new FieldString(schema, element, qualifier, language, authority, confidence);
    }


    public FieldString withElement(String element) {
        return new FieldString(schema, element, qualifier, language, authority, confidence);
    }


    public FieldString withQualifier(String qualifier) {
        return new FieldString(schema, element, qualifier, language, authority, confidence);
    }


    public FieldString withLanguage(String language) {
        return new FieldString(schema, element, qualifier, language, authority, confidence);
    }


    public FieldString withAuthority(String authority) {
        return new FieldString(schema, element, qualifier, language, authority, confidence);
    }


    public FieldString withConfidence(int confidence) {
        return new FieldString(schema, element, qualifier, language, authority, confidence);
    }

    public FieldString withWildCards(boolean signatureToo) {
        String wilcard = Item.ANY;
        return new FieldString(
                (signatureToo && StringUtils.isBlank(schema)) ? wilcard : schema,
                (signatureToo && StringUtils.isBlank(element)) ? wilcard : element,
                (signatureToo && StringUtils.isBlank(qualifier)) ? wilcard : qualifier,
                StringUtils.isBlank(language) ? wilcard : language,
                StringUtils.isBlank(authority) ? wilcard : authority,
                confidence
        );
    }

    public FieldString signatureOnly() {
        return new FieldString(schema, element, qualifier, null, null, Choices.CF_UNSET);
    }

    public FieldString filledWith(FieldString dcValue) {
        return new FieldString(
                StringUtils.isBlank(schema) ? dcValue.schema : schema,
                StringUtils.isBlank(element) ? dcValue.element : element,
                StringUtils.isBlank(qualifier) ? dcValue.qualifier : qualifier,
                StringUtils.isBlank(language) ? dcValue.language : language,
                StringUtils.isBlank(authority) ? dcValue.authority : authority,
                confidence == Choices.CF_UNSET ? dcValue.confidence : confidence
        );
    }
}
