package org.dspace.rest.common;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.core.Context;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: peterdietz
 * Date: 5/22/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "community")
public class Community extends DSpaceObject{
    private static Logger log = Logger.getLogger(Community.class);

    //Exandable relationships
    @XmlElement(name = "parentCommunity")
    private DSpaceObject parentCommunity;

    private String copyrightText, introductoryText, shortDescription, sidebarText;
    private Integer countItems;

    @XmlElement(name = "subcommunities", required = true)
    private List<DSpaceObject> subCommunities = new ArrayList<DSpaceObject>();

    @XmlElement(name = "collections")
    private List<DSpaceObject> collections = new ArrayList<DSpaceObject>();

    public Community(){}

    public Community(org.dspace.content.Community community, String expand, Context context) throws SQLException, WebApplicationException{
        super(community);
        setup(community, expand, context);
    }

    private void setup(org.dspace.content.Community community, String expand, Context context) throws SQLException{
        List<String> expandFields = new ArrayList<String>();
        if(expand != null) {
            expandFields = Arrays.asList(expand.split(","));
        }

        this.setCopyrightText(community.getMetadata(org.dspace.content.Community.COPYRIGHT_TEXT));
        this.setIntroductoryText(community.getMetadata(org.dspace.content.Community.INTRODUCTORY_TEXT));
        this.setSidebarText(community.getMetadata(org.dspace.content.Community.SIDEBAR_TEXT));
        this.setCountItems(community.countItems());

        if(expandFields.contains("parentCommunityID") || expandFields.contains("all")) {
            org.dspace.content.Community parentCommunity = community.getParentCommunity();
            if(parentCommunity != null) {
                setParentCommunity(new DSpaceObject(parentCommunity));
            }
        } else {
            this.addExpand("parentCommunityID");
        }

        if(expandFields.contains("subCollections") || expandFields.contains("all")) {
            org.dspace.content.Collection[] collectionArray = community.getCollections();
            collections = new ArrayList<DSpaceObject>();
            for(org.dspace.content.Collection collection : collectionArray) {
                if(AuthorizeManager.authorizeActionBoolean(context, collection, org.dspace.core.Constants.READ)) {
                    collections.add(new DSpaceObject(collection));
                } else {
                    log.info("Omitted restricted collection: " + collection.getID() + " _ " + collection.getName());
                }
            }
        } else {
            this.addExpand("subCollections");
        }

        if(expandFields.contains("subCommunities") || expandFields.contains("all")) {
            org.dspace.content.Community[] communityArray = community.getSubcommunities();
            subCommunities = new ArrayList<DSpaceObject>();
            for(org.dspace.content.Community subCommunity : communityArray) {
                if(AuthorizeManager.authorizeActionBoolean(context, subCommunity, org.dspace.core.Constants.READ)) {
                    subCommunities.add(new DSpaceObject(subCommunity));
                } else {
                    log.info("Omitted restricted subCommunity: " + subCommunity.getID() + " _ " + subCommunity.getName());
                }
            }
        } else {
            this.addExpand("subCommunities");
        }

        if(!expandFields.contains("all")) {
            this.addExpand("all");
        }
    }

    public List<DSpaceObject> getCollections() {
        return collections;
    }

    public void setCollections(List<DSpaceObject> collections) {
        this.collections = collections;
    }

    public Integer getCountItems() {
        return countItems;
    }

    public void setCountItems(Integer countItems) {
        this.countItems = countItems;
    }

    public String getSidebarText() {
        return sidebarText;
    }

    public void setSidebarText(String sidebarText) {
        this.sidebarText = sidebarText;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getIntroductoryText() {
        return introductoryText;
    }

    public void setIntroductoryText(String introductoryText) {
        this.introductoryText = introductoryText;
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public void setCopyrightText(String copyrightText) {
        this.copyrightText = copyrightText;
    }

    public DSpaceObject getParentCommunity() {
        return parentCommunity;
    }

    public void setParentCommunity(DSpaceObject parentCommunity) {
        this.parentCommunity = parentCommunity;
    }
}
