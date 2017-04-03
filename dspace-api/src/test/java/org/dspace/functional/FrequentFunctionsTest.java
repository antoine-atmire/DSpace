package org.dspace.functional;

import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.functional.metadata.FieldString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.dspace.functional.metadata.Fields.DC_CONTRIBUTOR_AUTHOR;
import static org.dspace.functional.metadata.Fields.DC_TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 01 Mar 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class FrequentFunctionsTest {

    @Mock
    private MetadataSchema dcSchema;

    @Mock
    private MetadataField titleField;

    @Mock
    private MetadataField authorField;

    @Mock
    private MetadataValue title1;

    @Mock
    private MetadataValue title2;

    @Mock
    private MetadataValue author1;

    @Mock
    private MetadataValue author2;

    private LinkedList<MetadataValue> testValues;

    protected void mockField(MetadataField field, MetadataSchema schema, String element, String qualifier) {
        when(field.getMetadataSchema()).thenReturn(schema);
        when(field.getElement()).thenReturn(element);
        when(field.getQualifier()).thenReturn(qualifier);
    }

    private void mockValue(MetadataValue metadatum, MetadataField field, String value, String language, String authority, int confidence) {
        when(metadatum.getMetadataField()).thenReturn(field);
        when(metadatum.getValue()).thenReturn(value);
        when(metadatum.getLanguage()).thenReturn(language);
        when(metadatum.getAuthority()).thenReturn(authority);
        when(metadatum.getConfidence()).thenReturn(confidence);
    }

    @Before
    public void setUp() throws Exception {
        when(dcSchema.getName()).thenReturn("dc");

        mockField(titleField, dcSchema, "title", null);
        mockField(authorField, dcSchema, "contributor", "author");

        mockValue(title1, titleField, "Testing saves lifes!", "en", null, Choices.CF_UNSET);
        mockValue(title2, titleField, "Testen red levens!", "nl", null, Choices.CF_UNSET);
        mockValue(author1, authorField, "Donald, Smith J.", null, "authoritykey", Choices.CF_ACCEPTED);
        mockValue(author2, authorField, "Musk, Elon", null, null, Choices.CF_UNSET);

        testValues = new LinkedList<MetadataValue>() {{
            add(title1);
            add(title2);
            add(author1);
            add(author2);
        }};
    }


    @Test
    public void getValues() throws Exception {
        List<? extends String> values = testValues.stream()
                .map(FrequentFunctions.getValues())
                .collect(Collectors.toList());

        assertEquals(4, values.size());
        assertTrue(values.contains("Testing saves lifes!"));
        assertTrue(values.contains("Testen red levens!"));
        assertTrue(values.contains("Donald, Smith J."));
        assertTrue(values.contains("Musk, Elon"));
    }

    @Test
    public void filterByField() throws Exception {
        List<MetadataValue> collect = testValues.stream()
                .filter(FrequentFunctions.filterByField(DC_TITLE))
                .collect(Collectors.toList());

        assertEquals(2, collect.size());
        assertTrue(collect.contains(title1));
        assertTrue(collect.contains(title2));
        assertFalse(collect.contains(author1));
        assertFalse(collect.contains(author2));


        collect = testValues.stream()
                .filter(FrequentFunctions.filterByField(DC_CONTRIBUTOR_AUTHOR))
                .collect(Collectors.toList());

        assertEquals(2, collect.size());
        assertFalse(collect.contains(title1));
        assertFalse(collect.contains(title2));
        assertTrue(collect.contains(author1));
        assertTrue(collect.contains(author2));
    }

    @Test
    public void filterByLanguage() throws Exception {
        List<MetadataValue> collect = testValues.stream()
                .filter(FrequentFunctions.filterByLanguage("en"))
                .collect(Collectors.toList());

        assertEquals(1, collect.size());
        assertTrue(collect.contains(title1));
        assertFalse(collect.contains(title2));
        assertFalse(collect.contains(author1));
        assertFalse(collect.contains(author2));
    }

    @Test
    public void filterByConfidence() throws Exception {
        List<MetadataValue> collect = testValues.stream()
                .filter(FrequentFunctions.filterBy(Choices.CF_ACCEPTED, MetadataValue::getConfidence))
                .collect(Collectors.toList());

        assertEquals(1, collect.size());
        assertTrue(collect.contains(author1));
    }

    @Test
    public void sortByValue() throws Exception {
        List<MetadataValue> collect = testValues.stream()
                .sorted(FrequentFunctions.compareByValue())
                .collect(Collectors.toList());

        assertEquals(4, collect.size());
        assertEquals(0, collect.indexOf(author1));
        assertEquals(1, collect.indexOf(author2));
        assertEquals(2, collect.indexOf(title2));
        assertEquals(3, collect.indexOf(title1));
    }

    @Test
    public void sortByAuthority() throws Exception {
        List<MetadataValue> collect = testValues.stream()
                .sorted(FrequentFunctions.compareBy(MetadataValue::getAuthority))
                .collect(Collectors.toList());

        assertEquals(4, collect.size());
        assertEquals(0, collect.indexOf(author1));
    }

    @Test
    public void sortByFieldThenValue() throws Exception {
        List<MetadataValue> collect = testValues.stream()
                .sorted(FrequentFunctions.compareBy(
                        FrequentFunctions.compareSignature(),
                        MetadataValue::getValue
                ))
                .collect(Collectors.toList());

        assertEquals(4, collect.size());
        assertEquals(0, collect.indexOf(author1));
        assertEquals(1, collect.indexOf(author2));
        assertEquals(2, collect.indexOf(title2));
        assertEquals(3, collect.indexOf(title1));
    }
}