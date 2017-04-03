package org.dspace.functional.metadata;

import org.dspace.content.Item;
import org.dspace.content.authority.Choices;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 02 Mar 2017
 */
public class FieldStringTest {


    private FieldString titleField;

    @Before
    public void setUp() throws Exception {
        titleField = new FieldString("dc.title[en]::*::*");
    }

    @Test
    public void getSchema() throws Exception {
       assertEquals("dc", titleField.getSchema());
    }

    @Test
    public void getElement() throws Exception {
        assertEquals("title", titleField.getElement());
    }

    @Test
    public void getQualifier() throws Exception {
        assertEquals(null, titleField.getQualifier());

    }

    @Test
    public void getLanguage() throws Exception {
        assertEquals("en", titleField.getLanguage());
    }

    @Test
    public void getAuthority() throws Exception {
        assertEquals(Item.ANY, titleField.getAuthority());

    }

    @Test
    public void getConfidence() throws Exception {
        assertEquals(Choices.CF_UNSET, titleField.getConfidence());
    }

    @Test
    public void withSchema() throws Exception {
        assertEquals("dcterms", (titleField.withSchema("dcterms").getSchema()));
    }

    @Test
    public void withElement() throws Exception {
        assertEquals("subtitle", (titleField.withElement("subtitle").getElement()));
    }

    @Test
    public void withQualifier() throws Exception {
        assertEquals("alternative", (titleField.withQualifier("alternative").getQualifier()));
    }

    @Test
    public void withLanguage() throws Exception {
        assertEquals("en", (titleField.withLanguage("en").getLanguage()));
    }

    @Test
    public void withAuthority() throws Exception {
        assertEquals("authoritykey", (titleField.withAuthority("authoritykey").getAuthority()));
    }

    @Test
    public void withConfidence() throws Exception {
        assertEquals(Choices.CF_ACCEPTED, (titleField.withConfidence(Choices.CF_ACCEPTED).getConfidence()));
    }

    @Test
    public void withWildcardsTrue() throws Exception {
        assertEquals(Item.ANY, (titleField.withWildCards(true).getQualifier()));
    }
    @Test
    public void withWildcardsFalse() throws Exception {
        assertNotEquals(Item.ANY, (titleField.withWildCards(false).getQualifier()));
    }

    @Test
    public void filledWith() throws Exception {
        FieldString alternative = new FieldString("dc.title.alternative[*]::*::*");
        assertEquals("alternative", (titleField.filledWith(alternative).getQualifier()));
    }

}