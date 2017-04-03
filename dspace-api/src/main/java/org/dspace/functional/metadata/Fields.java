package org.dspace.functional.metadata;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 02 Mar 2017
 */
public class Fields {

    public final static FieldString DC_CONTRIBUTOR                                  = forSignature("dc.contributor");
    public final static FieldString DC_CONTRIBUTOR_ADVISOR                          = forSignature("dc.contributor.advisor");
    public final static FieldString DC_CONTRIBUTOR_AUTHOR                           = forSignature("dc.contributor.author");
    public final static FieldString DC_CONTRIBUTOR_EDITOR                           = forSignature("dc.contributor.editor");
    public final static FieldString DC_CONTRIBUTOR_ILLUSTRATOR                      = forSignature("dc.contributor.illustrator");
    public final static FieldString DC_CONTRIBUTOR_OTHER                            = forSignature("dc.contributor.other");
    public final static FieldString DC_COVERAGE_SPATIAL                             = forSignature("dc.coverage.spatial");
    public final static FieldString DC_COVERAGE_TEMPORAL                            = forSignature("dc.coverage.temporal");
    public final static FieldString DC_CREATOR                                      = forSignature("dc.creator");
    public final static FieldString DC_DATE                                         = forSignature("dc.date");
    public final static FieldString DC_DATE_ACCESSIONED                             = forSignature("dc.date.accessioned");
    public final static FieldString DC_DATE_AVAILABLE                               = forSignature("dc.date.available");
    public final static FieldString DC_DATE_COPYRIGHT                               = forSignature("dc.date.copyright");
    public final static FieldString DC_DATE_CREATED                                 = forSignature("dc.date.created");
    public final static FieldString DC_DATE_ISSUED                                  = forSignature("dc.date.issued");
    public final static FieldString DC_DATE_SUBMITTED                               = forSignature("dc.date.submitted");
    public final static FieldString DC_DESCRIPTION                                  = forSignature("dc.description");
    public final static FieldString DC_DESCRIPTION_ABSTRACT                         = forSignature("dc.description.abstract");
    public final static FieldString DC_DESCRIPTION_PROVENANCE                       = forSignature("dc.description.provenance");
    public final static FieldString DC_DESCRIPTION_SPONSORSHIP                      = forSignature("dc.description.sponsorship");
    public final static FieldString DC_DESCRIPTION_STATEMENTOFRESPONSIBILITY        = forSignature("dc.description.statementofresponsibility");
    public final static FieldString DC_DESCRIPTION_TABLEOFCONTENTS                  = forSignature("dc.description.tableofcontents");
    public final static FieldString DC_DESCRIPTION_URI                              = forSignature("dc.description.uri");
    public final static FieldString DC_FORMAT                                       = forSignature("dc.format");
    public final static FieldString DC_FORMAT_EXTENT                                = forSignature("dc.format.extent");
    public final static FieldString DC_FORMAT_MEDIUM                                = forSignature("dc.format.medium");
    public final static FieldString DC_FORMAT_MIMETYPE                              = forSignature("dc.format.mimetype");
    public final static FieldString DC_IDENTIFIER                                   = forSignature("dc.identifier");
    public final static FieldString DC_IDENTIFIER_CITATION                          = forSignature("dc.identifier.citation");
    public final static FieldString DC_IDENTIFIER_GOVDOC                            = forSignature("dc.identifier.govdoc");
    public final static FieldString DC_IDENTIFIER_ISBN                              = forSignature("dc.identifier.isbn");
    public final static FieldString DC_IDENTIFIER_ISMN                              = forSignature("dc.identifier.ismn");
    public final static FieldString DC_IDENTIFIER_ISSN                              = forSignature("dc.identifier.issn");
    public final static FieldString DC_IDENTIFIER_OTHER                             = forSignature("dc.identifier.other");
    public final static FieldString DC_IDENTIFIER_SICI                              = forSignature("dc.identifier.sici");
    public final static FieldString DC_IDENTIFIER_URI                               = forSignature("dc.identifier.uri");
    public final static FieldString DC_LANGUAGE                                     = forSignature("dc.language");
    public final static FieldString DC_LANGUAGE_ISO                                 = forSignature("dc.language.iso");
    public final static FieldString DC_PROVENANCE                                   = forSignature("dc.provenance");
    public final static FieldString DC_PUBLISHER                                    = forSignature("dc.publisher");
    public final static FieldString DC_RELATION                                     = forSignature("dc.relation");
    public final static FieldString DC_RELATION_HASPART                             = forSignature("dc.relation.haspart");
    public final static FieldString DC_RELATION_HASVERSION                          = forSignature("dc.relation.hasversion");
    public final static FieldString DC_RELATION_ISBASEDON                           = forSignature("dc.relation.isbasedon");
    public final static FieldString DC_RELATION_ISFORMATOF                          = forSignature("dc.relation.isformatof");
    public final static FieldString DC_RELATION_ISPARTOF                            = forSignature("dc.relation.ispartof");
    public final static FieldString DC_RELATION_ISPARTOFSERIES                      = forSignature("dc.relation.ispartofseries");
    public final static FieldString DC_RELATION_ISREFERENCEDBY                      = forSignature("dc.relation.isreferencedby");
    public final static FieldString DC_RELATION_ISREPLACEDBY                        = forSignature("dc.relation.isreplacedby");
    public final static FieldString DC_RELATION_ISVERSIONOF                         = forSignature("dc.relation.isversionof");
    public final static FieldString DC_RELATION_REPLACES                            = forSignature("dc.relation.replaces");
    public final static FieldString DC_RELATION_REQUIRES                            = forSignature("dc.relation.requires");
    public final static FieldString DC_RELATION_URI                                 = forSignature("dc.relation.uri");
    public final static FieldString DC_RIGHTS                                       = forSignature("dc.rights");
    public final static FieldString DC_RIGHTS_LICENSE                               = forSignature("dc.rights.license");
    public final static FieldString DC_RIGHTS_URI                                   = forSignature("dc.rights.uri");
    public final static FieldString DC_SOURCE                                       = forSignature("dc.source");
    public final static FieldString DC_SOURCE_URI                                   = forSignature("dc.source.uri");
    public final static FieldString DC_SUBJECT                                      = forSignature("dc.subject");
    public final static FieldString DC_SUBJECT_CLASSIFICATION                       = forSignature("dc.subject.classification");
    public final static FieldString DC_SUBJECT_DDC                                  = forSignature("dc.subject.ddc");
    public final static FieldString DC_SUBJECT_LCC                                  = forSignature("dc.subject.lcc");
    public final static FieldString DC_SUBJECT_LCSH                                 = forSignature("dc.subject.lcsh");
    public final static FieldString DC_SUBJECT_MESH                                 = forSignature("dc.subject.mesh");
    public final static FieldString DC_SUBJECT_OTHER                                = forSignature("dc.subject.other");
    public final static FieldString DC_TITLE                                        = forSignature("dc.title");
    public final static FieldString DC_TITLE_ALTERNATIVE                            = forSignature("dc.title.alternative");
    public final static FieldString DC_TYPE                                         = forSignature("dc.type");


    public static FieldString full(String field) {
        return new FieldString(field);
    }

    public static FieldString forSignature(String field) {
        return new FieldString(field).withWildCards(false);
    }

}
