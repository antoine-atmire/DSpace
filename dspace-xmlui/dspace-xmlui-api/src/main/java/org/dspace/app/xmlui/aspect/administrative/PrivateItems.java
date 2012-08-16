/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.administrative;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.util.HashUtil;
import org.apache.excalibur.source.SourceValidity;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.DSpaceValidity;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.RequestUtils;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Cell;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Para;
import org.dspace.app.xmlui.wing.element.ReferenceSet;
import org.dspace.app.xmlui.wing.element.Row;
import org.dspace.app.xmlui.wing.element.Select;
import org.dspace.app.xmlui.wing.element.Table;
import org.dspace.authorize.AuthorizeException;
import org.dspace.browse.BrowseEngine;
import org.dspace.browse.BrowseException;
import org.dspace.browse.BrowseIndex;
import org.dspace.browse.BrowseInfo;
import org.dspace.browse.BrowseItem;
import org.dspace.browse.BrowserScope;
import org.dspace.sort.SortOption;
import org.dspace.sort.SortException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DCDate;
import org.dspace.content.DSpaceObject;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.xml.sax.SAXException;

/**
 * Implements all the browse functionality (browse by title, subject, authors,
 * etc.) The types of browse available are configurable by the implementor. See
 * dspace.cfg and documentation for instructions on how to configure.
 *
 * @author Graham Triggs
 */
public class PrivateItems extends AbstractDSpaceTransformer implements
        CacheableProcessingComponent
{
    private static final Logger log = Logger.getLogger(PrivateItems.class);

    /**
     * Static Messages for common text
     */
    private static final Message T_dspace_home = message("xmlui.general.dspace_home");

    private static final Message T_go = message("xmlui.general.go");

    private static final Message T_update = message("xmlui.general.update");

    private static final Message T_choose_month = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.choose_month");

    private static final Message T_choose_year = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.choose_year");

    private static final Message T_jump_year = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.jump_year");

    private static final Message T_jump_year_help = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.jump_year_help");

    private static final Message T_jump_select = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.jump_select");

    private static final Message T_starts_with = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.starts_with");

    private static final Message T_starts_with_help = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.starts_with_help");

    private static final Message T_sort_by = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.sort_by");

    private static final Message T_order = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.order");

    private static final Message T_rpp = message("xmlui.ArtifactBrowser.ConfigurableBrowse.general.rpp");

    private static final Message T_order_asc = message("xmlui.ArtifactBrowser.ConfigurableBrowse.order.asc");

    private static final Message T_order_desc = message("xmlui.ArtifactBrowser.ConfigurableBrowse.order.desc");

    private static final String PRIVATE_URL_BASE = "private";

    /**
     * These variables dictate when the drop down list of years is to break from
     * 1 year increments, to 5 year increments, to 10 year increments, and
     * finally to stop.
     */
    private static final int ONE_YEAR_LIMIT = 10;

    private static final int FIVE_YEAR_LIMIT = 30;

    private static final int TEN_YEAR_LIMIT = 100;

    /** Cached validity object */
    private SourceValidity validity;

    /** Cached UI parameters, results and messages */
    private BrowseParams userParams;

    private BrowseInfo browseInfo;

    private Message titleMessage = null;
    private Message trailMessage = null;

    public Serializable getKey()
    {
        try
        {
            BrowseParams params = getUserParams();

            String key = params.getKey();

            if (key != null)
            {
                DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
                if (dso != null)
                {
                    key += "-" + dso.getHandle();
                }

                return HashUtil.hash(key);
            }
        }
        catch (SQLException e)
        {
            log.error("Database error", e);
        }
        catch (UIException e)
        {
            log.error("UI error", e);
        }

        return "0";
    }

    public SourceValidity getValidity()
    {
        if (validity == null)
        {
            try
            {
                DSpaceValidity newValidity = new DSpaceValidity();
                DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

                if (dso != null)
                {
                    newValidity.add(dso);
                }

                BrowseInfo info = getBrowseInfo();

                // Are we browsing items, or unique metadata?
                if (isItemBrowse(info))
                {
                    // Add the browse items to the validity
                    for (BrowseItem item : (java.util.List<BrowseItem>) info.getResults())
                    {
                        newValidity.add(item);
                    }
                }
                else
                {
                    // Add the metadata to the validity
                    for (String[] singleEntry : browseInfo.getStringResults())
                    {
                        newValidity.add(singleEntry[0]+"#"+singleEntry[1]);
                    }
                }

                validity = newValidity;
            }
            catch (SQLException e)
            {
                log.error("Database error", e);
            }
            catch (UIException e)
            {
                log.error("UI error", e);
            }

        }

        return this.validity;
    }

    /**
     * Add Page metadata.
     */
    public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException,
            SQLException, IOException, AuthorizeException
    {
        BrowseInfo info = getBrowseInfo();

        pageMeta.addMetadata("title").addContent(getTitleMessage(info));

        DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

        pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
        if (dso != null)
        {
            HandleUtil.buildHandleTrail(dso, pageMeta, contextPath);
        }

        pageMeta.addTrail().addContent(getTrailMessage(info));
    }

    /**
     * Add the browse-title division.
     */
    public void addBody(Body body) throws SAXException, WingException, UIException, SQLException,
            IOException, AuthorizeException
    {
        BrowseParams params = getUserParams();
        BrowseInfo info = getBrowseInfo();

        String type = "private";

        // Build the DRI Body
        Division div = body.addDivision("browse-by-" + type, "primary");

        div.setHead(getTitleMessage(info));

        // Build the internal navigation (jump lists)
        addBrowseJumpNavigation(div, info, params);

        // Build the sort and display controls
        addBrowseControls(div, info, params);

        // This div will hold the browsing results
        Division results = div.addDivision("browse-by-" + type + "-results", "primary");

        // Add the pagination
        if (info.getTotal() <= 0)
        {
            results.setSimplePagination(0, 0, 0, null, null);

        }
        else
        {
            results.setSimplePagination(info.getTotal(), browseInfo.getOverallPosition() + 1,
                    browseInfo.getOverallPosition() + browseInfo.getResultCount(), getPreviousPageURL(
                    params, info), getNextPageURL(params, info));
        }

        // Reference all the browsed items
        ReferenceSet referenceSet = results.addReferenceSet("browse-by-" + type,
                ReferenceSet.TYPE_SUMMARY_LIST, type, null);

        // Are we browsing items, or unique metadata?
        if (isItemBrowse(info))
        {
            // Add the items to the browse results
            for (BrowseItem item : (java.util.List<BrowseItem>) info.getResults())
            {
                referenceSet.addReference(item);
            }
        }
        else    // browsing a list of unique metadata entries
        {
            // Create a table for the results
            Table singleTable = results.addTable("browse-by-" + type + "-results",
                    browseInfo.getResultCount() + 1, 1);

            // Add the column heading
            singleTable.addRow(Row.ROLE_HEADER).addCell().addContent(
                    message("xmlui.ArtifactBrowser.ConfigurableBrowse." + type + ".column_heading"));

            // Iterate each result
            for (String[] singleEntry : browseInfo.getStringResults())
            {
                // Create a Map of the query parameters for the link
                Map<String, String> queryParams = new HashMap<String, String>();
                queryParams.put(BrowseParams.TYPE, encodeForURL(type));
                if (singleEntry[1] != null)
                {
                    queryParams.put(BrowseParams.FILTER_VALUE[1], encodeForURL(
                            singleEntry[1]));
                }
                else
                {
                    queryParams.put(BrowseParams.FILTER_VALUE[0], encodeForURL(
                            singleEntry[0]));
                }
                // Create an entry in the table, and a linked entry
                Cell cell = singleTable.addRow().addCell();
                cell.addXref(super.generateURL(PRIVATE_URL_BASE, queryParams), singleEntry[0]);
            }
        }
    }

    /**
     * Recycle
     */
    public void recycle()
    {
        this.validity = null;
        this.userParams = null;
        this.browseInfo = null;
        this.titleMessage = null;
        this.trailMessage = null;
        super.recycle();
    }

    /**
     * Makes the jump-list navigation for the results
     *
     * @param div
     * @param info
     * @param params
     * @throws WingException
     */
    private void addBrowseJumpNavigation(Division div, BrowseInfo info, BrowseParams params)
            throws WingException
    {
        // Prepare a Map of query parameters required for all links
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.putAll(params.getCommonParameters());
        queryParams.putAll(params.getControlParameters());

        // Navigation aid (really this is a poor version of pagination)
        Division jump = div.addInteractiveDivision("browse-navigation", PRIVATE_URL_BASE,
                Division.METHOD_POST, "secondary navigation");

        // Add all the query parameters as hidden fields on the form
        for (Map.Entry<String, String> param : queryParams.entrySet())
        {
            jump.addHidden(param.getKey()).setValue(param.getValue());
        }

        // If this is a date based browse, render the date navigation
        if (isSortedByDate(info))
        {
            Para jumpForm = jump.addPara();

            // Create a select list to choose a month
            jumpForm.addContent(T_jump_select);
            Select month = jumpForm.addSelect(BrowseParams.MONTH);
            month.addOption(false, "-1", T_choose_month);
            for (int i = 1; i <= 12; i++)
            {
                month.addOption(false, String.valueOf(i), DCDate.getMonthName(i, Locale
                        .getDefault()));
            }

            // Create a select list to choose a year
            Select year = jumpForm.addSelect(BrowseParams.YEAR);
            year.addOption(false, "-1", T_choose_year);
            int currentYear = DCDate.getCurrent().getYear();
            int i = currentYear;

            // Calculate where to move from 1, 5 to 10 year jumps
            int oneYearBreak = ((currentYear - ONE_YEAR_LIMIT) / 5) * 5;
            int fiveYearBreak = ((currentYear - FIVE_YEAR_LIMIT) / 10) * 10;
            int tenYearBreak = (currentYear - TEN_YEAR_LIMIT);
            do
            {
                year.addOption(false, String.valueOf(i), String.valueOf(i));

                if (i <= fiveYearBreak)
                {
                    i -= 10;
                }
                else if (i <= oneYearBreak)
                {
                    i -= 5;
                }
                else
                {
                    i--;
                }
            }
            while (i > tenYearBreak);

            // Create a free text entry box for the year
            jumpForm = jump.addPara();
            jumpForm.addContent(T_jump_year);
            jumpForm.addText(BrowseParams.STARTS_WITH).setHelp(T_jump_year_help);

            jumpForm.addButton("submit").setValue(T_go);
        }
        else
        {
            // Create a clickable list of the alphabet
            List jumpList = jump.addList("jump-list", List.TYPE_SIMPLE, "alphabet");
            for (char c = 'A'; c <= 'Z'; c++)
            {
                Map<String, String> cQuery = new HashMap<String, String>(queryParams);
                cQuery.put(BrowseParams.STARTS_WITH, Character.toString(c));
                jumpList.addItemXref(super.generateURL(PRIVATE_URL_BASE, cQuery), Character
                        .toString(c));
            }

            // Create a free text field for the initial characters
            Para jumpForm = jump.addPara();
            jumpForm.addContent(T_starts_with);
            jumpForm.addText(BrowseParams.STARTS_WITH).setHelp(T_starts_with_help);

            jumpForm.addButton("submit").setValue(T_go);
        }
    }

    /**
     * Add the controls to changing sorting and display options.
     *
     * @param div
     * @param info
     * @param params
     * @throws WingException
     */
    private void addBrowseControls(Division div, BrowseInfo info, BrowseParams params)
            throws WingException
    {
        // Prepare a Map of query parameters required for all links
        Map<String, String> queryParams = new HashMap<String, String>();

        queryParams.putAll(params.getCommonParameters());

        Division controls = div.addInteractiveDivision("browse-controls", PRIVATE_URL_BASE,
                Division.METHOD_POST, "browse controls");

        // Add all the query parameters as hidden fields on the form
        for (Map.Entry<String, String> param : queryParams.entrySet())
        {
            controls.addHidden(param.getKey()).setValue(param.getValue());
        }

        Para controlsForm = controls.addPara();

        // If we are browsing a list of items
        if (isItemBrowse(info)) //  && info.isSecondLevel()
        {
            try
            {
                // Create a drop down of the different sort columns available
                Set<SortOption> sortOptions = SortOption.getSortOptions();

                // Only generate the list if we have multiple columns
                if (sortOptions.size() > 1)
                {
                    controlsForm.addContent(T_sort_by);
                    Select sortSelect = controlsForm.addSelect(BrowseParams.SORT_BY);

                    for (SortOption so : sortOptions)
                    {
                        sortSelect.addOption(so.equals(info.getSortOption()), so.getNumber(),
                                message("xmlui.ArtifactBrowser.ConfigurableBrowse.sort_by." + so.getName()));
                    }
                }
            }
            catch (SortException se)
            {
                throw new WingException("Unable to get sort options", se);
            }
        }

        // Create a control to changing ascending / descending order
        controlsForm.addContent(T_order);
        Select orderSelect = controlsForm.addSelect(BrowseParams.ORDER);
        orderSelect.addOption("ASC".equals(params.scope.getOrder()), "ASC", T_order_asc);
        orderSelect.addOption("DESC".equals(params.scope.getOrder()), "DESC", T_order_desc);

        // Create a control for the number of records to display
        controlsForm.addContent(T_rpp);
        Select rppSelect = controlsForm.addSelect(BrowseParams.RESULTS_PER_PAGE);
        for (int i = 5; i <= 100; i += 5)
        {
            rppSelect.addOption((i == info.getResultsPerPage()), i, Integer.toString(i));
        }

        // Create a control for the number of authors per item to display
        // FIXME This is currently disabled, as the supporting functionality
        // is not currently present in xmlui
        //if (isItemBrowse(info))
        //{
        //    controlsForm.addContent(T_etal);
        //    Select etalSelect = controlsForm.addSelect(BrowseParams.ETAL);
        //
        //    etalSelect.addOption((info.getEtAl() < 0), 0, T_etal_all);
        //    etalSelect.addOption(1 == info.getEtAl(), 1, Integer.toString(1));
        //
        //    for (int i = 5; i <= 50; i += 5)
        //    {
        //        etalSelect.addOption(i == info.getEtAl(), i, Integer.toString(i));
        //    }
        //}

        controlsForm.addButton("update").setValue(T_update);
    }

    /**
     * The URL query string of of the previous page.
     *
     * Note: the query string does not start with a "?" or "&" those need to be
     * added as appropriate by the caller.
     */
    private String getPreviousPageURL(BrowseParams params, BrowseInfo info) throws SQLException,
            UIException
    {
        // Don't create a previous page link if this is the first page
        if (info.isFirst())
        {
            return null;
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.putAll(params.getCommonParameters());
        parameters.putAll(params.getControlParameters());

        if (info.hasPrevPage())
        {
            parameters.put(BrowseParams.OFFSET, encodeForURL(String.valueOf(info.getPrevOffset())));
        }

        return super.generateURL(PRIVATE_URL_BASE, parameters);

    }

    /**
     * The URL query string of of the next page.
     *
     * Note: the query string does not start with a "?" or "&" those need to be
     * added as appropriate by the caller.
     */
    private String getNextPageURL(BrowseParams params, BrowseInfo info) throws SQLException,
            UIException
    {
        // Don't create a next page link if this is the last page
        if (info.isLast())
        {
            return null;
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.putAll(params.getCommonParameters());
        parameters.putAll(params.getControlParameters());

        if (info.hasNextPage())
        {
            parameters.put(BrowseParams.OFFSET, encodeForURL(String.valueOf(info.getNextOffset())));
        }

        return super.generateURL(PRIVATE_URL_BASE, parameters);
    }

    /**
     * Get the query parameters supplied to the browse.
     *
     * @return
     * @throws SQLException
     * @throws UIException
     */
    private BrowseParams getUserParams() throws SQLException, UIException
    {
        if (this.userParams != null)
        {
            return this.userParams;
        }

        Context context = ContextUtil.obtainContext(objectModel);
        Request request = ObjectModelHelper.getRequest(objectModel);

        BrowseParams params = new BrowseParams();

        params.month = request.getParameter(BrowseParams.MONTH);
        params.year = request.getParameter(BrowseParams.YEAR);
        params.etAl = RequestUtils.getIntParameter(request, BrowseParams.ETAL);

        params.scope = new BrowserScope(context);

        // Are we in a community or collection?
        DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
        if (dso instanceof Community)
        {
            params.scope.setCommunity((Community) dso);
        }
        if (dso instanceof Collection)
        {
            params.scope.setCollection((Collection) dso);
        }

        try
        {
            int    sortBy = RequestUtils.getIntParameter(request, BrowseParams.SORT_BY);

            BrowseIndex bi = BrowseIndex.getPrivateBrowseIndex();

            // If we don't have a sort column
            if (sortBy == -1)
            {
                // Get the default one
                SortOption so = bi.getSortOption();
                if (so != null)
                {
                    sortBy = so.getNumber();
                }
            }

            params.scope.setBrowseIndex(bi);
            params.scope.setSortBy(sortBy);

            params.scope.setJumpToItem(RequestUtils.getIntParameter(request, BrowseParams.JUMPTO_ITEM));
            params.scope.setOrder(request.getParameter(BrowseParams.ORDER));
            params.scope.setOffset(RequestUtils.getIntParameter(request, BrowseParams.OFFSET));
            params.scope.setResultsPerPage(RequestUtils.getIntParameter(request,
                    BrowseParams.RESULTS_PER_PAGE));
            params.scope.setStartsWith(request.getParameter(BrowseParams.STARTS_WITH));
            String filterValue = request.getParameter(BrowseParams.FILTER_VALUE[0]);
            if (filterValue == null)
            {
                filterValue = request.getParameter(BrowseParams.FILTER_VALUE[1]);
            }
            else
            {
                params.scope.setAuthorityValue(filterValue);
            }
            params.scope.setFilterValue(filterValue);
            params.scope.setJumpToValue(request.getParameter(BrowseParams.JUMPTO_VALUE));
            params.scope.setJumpToValueLang(request.getParameter(BrowseParams.JUMPTO_VALUE_LANG));
            params.scope.setFilterValueLang(request.getParameter(BrowseParams.FILTER_VALUE_LANG));

            // Filtering to a value implies this is a second level browse
            if (params.scope.getFilterValue() != null)
            {
                params.scope.setBrowseLevel(1);
            }

            // if year and perhaps month have been selected, we translate these
            // into "startsWith"
            // if startsWith has already been defined then it is overwritten
            if (params.year != null && !"".equals(params.year) && !"-1".equals(params.year))
            {
                String startsWith = params.year;
                if ((params.month != null) && !"-1".equals(params.month)
                        && !"".equals(params.month))
                {
                    // subtract 1 from the month, so the match works
                    // appropriately
                    if ("ASC".equals(params.scope.getOrder()))
                    {
                        params.month = Integer.toString((Integer.parseInt(params.month) - 1));
                    }

                    // They've selected a month as well
                    if (params.month.length() == 1)
                    {
                        // Ensure double-digit month number
                        params.month = "0" + params.month;
                    }

                    startsWith = params.year + "-" + params.month;

                    if ("ASC".equals(params.scope.getOrder()))
                    {
                        startsWith = startsWith + "-32";
                    }
                }

                params.scope.setStartsWith(startsWith);
            }
        }
        catch (BrowseException bex)
        {
            throw new UIException("Unable to create browse parameters", bex);
        }

        this.userParams = params;
        return params;
    }

    /**
     * Get the results of the browse. If the results haven't been generated yet,
     * then this will perform the browse.
     *
     * @return
     * @throws SQLException
     * @throws UIException
     */
    private BrowseInfo getBrowseInfo() throws SQLException, UIException
    {
        if (this.browseInfo != null)
        {
            return this.browseInfo;
        }

        Context context = ContextUtil.obtainContext(objectModel);

        // Get the parameters we will use for the browse
        // (this includes a browse scope)
        BrowseParams params = getUserParams();

        try
        {
            // Create a new browse engine, and perform the browse
            BrowseEngine be = new BrowseEngine(context);
            this.browseInfo = be.browse(params.scope);

            // figure out the setting for author list truncation
            if (params.etAl < 0)
            {
                // there is no limit, or the UI says to use the default
                int etAl = ConfigurationManager.getIntProperty("webui.browse.author-limit");
                if (etAl != 0)
                {
                    this.browseInfo.setEtAl(etAl);
                }

            }
            else if (params.etAl == 0) // 0 is the user setting for unlimited
            {
                this.browseInfo.setEtAl(-1); // but -1 is the application
                // setting for unlimited
            }
            else
            // if the user has set a limit
            {
                this.browseInfo.setEtAl(params.etAl);
            }
        }
        catch (BrowseException bex)
        {
            throw new UIException("Unable to process browse", bex);
        }

        return this.browseInfo;
    }

    /**
     * Is this a browse on a list of items, or unique metadata values?
     *
     * @param info
     * @return
     */
    private boolean isItemBrowse(BrowseInfo info)
    {
        return info.getBrowseIndex().isItemIndex() || info.isSecondLevel();
    }

    /**
     * Is this browse sorted by date?
     * @param info
     * @return
     */
    private boolean isSortedByDate(BrowseInfo info)
    {
        return info.getSortOption().isDate() ||
                (info.getBrowseIndex().isDate() && info.getSortOption().isDefault());
    }

    private Message getTitleMessage(BrowseInfo info)
    {
        if (titleMessage == null)
        {
            BrowseIndex bix = info.getBrowseIndex();

            // For a second level browse (ie. items for author),
            // get the value we are focussing on (ie. author).
            // (empty string if none).
            String value = (info.hasValue() ? "\"" + info.getValue() + "\"" : "");

            // Get the name of any scoping element (collection / community)
            String scopeName = "";

            if (info.getBrowseContainer() != null)
            {
                scopeName = info.getBrowseContainer().getName();
            }
            else
            {
                scopeName = "";
            }

            if (bix.isMetadataIndex())
            {
                titleMessage = message("xmlui.ArtifactBrowser.ConfigurableBrowse.title.metadata." + bix.getName())
                        .parameterize(scopeName, value);
            }
            else if (info.getSortOption() != null)
            {
                titleMessage = message("xmlui.ArtifactBrowser.ConfigurableBrowse.title.item." + info.getSortOption().getName())
                        .parameterize(scopeName, value);
            }
            else
            {
                titleMessage = message("xmlui.ArtifactBrowser.ConfigurableBrowse.title.item." + bix.getSortOption().getName())
                        .parameterize(scopeName, value);
            }
        }

        return titleMessage;
    }

    private Message getTrailMessage(BrowseInfo info)
    {
        if (trailMessage == null)
        {
            BrowseIndex bix = info.getBrowseIndex();

            // Get the name of any scoping element (collection / community)
            String scopeName = "";

            if (info.getBrowseContainer() != null)
            {
                scopeName = info.getBrowseContainer().getName();
            }
            else
            {
                scopeName = "";
            }

            if (bix.isMetadataIndex())
            {
                trailMessage = message("xmlui.ArtifactBrowser.ConfigurableBrowse.trail.metadata." + bix.getName())
                        .parameterize(scopeName);
            }
            else if (info.getSortOption() != null)
            {
                trailMessage = message("xmlui.ArtifactBrowser.ConfigurableBrowse.trail.item." + info.getSortOption().getName())
                        .parameterize(scopeName);
            }
            else
            {
                trailMessage = message("xmlui.ArtifactBrowser.ConfigurableBrowse.trail.item." + bix.getSortOption().getName())
                        .parameterize(scopeName);
            }
        }

        return trailMessage;
    }
}