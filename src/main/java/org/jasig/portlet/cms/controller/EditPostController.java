/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.cms.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.portlet.PortletRequestContext;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.LoadScheduledPostsResponse;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RemovePostAttachmentResponse;
import org.jasig.portlet.cms.model.RemoveScheduledPostResponse;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.jasig.portlet.cms.model.repository.JcrRepositoryException;
import org.jasig.portlet.cms.model.repository.schedule.ScheduledPostsManager;
import org.jasig.portlet.cms.util.PortletUtilities;
import org.jasig.portlet.cms.view.PortletView;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.multipart.MultipartActionRequest;
import org.springframework.web.portlet.util.PortletUtils;

@Controller
@RequestMapping("EDIT")
public class EditPostController extends AbstractPortletController {

    private Validator validator = null;

    private Validator getValidator() {
        return this.validator;
    }

    @Autowired
    protected void setValidator(Validator validator) {
        this.validator = validator;
    }

    private void processPostAttachments(final ActionRequest request, final Post post) throws Exception {
        if (FileUploadBase.isMultipartContent(new PortletRequestContext(request))) {

            /*
             * Attachments may have been removed in the edit mode. We must
             * refresh the session-bound post before updating attachments.
             */
            final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
            final Post originalPost = getRepositoryDao().getPost(pref.getPortletRepositoryRoot());

            if (originalPost != null) {
                post.getAttachments().clear();
                post.getAttachments().addAll(originalPost.getAttachments());
            }

            final MultipartActionRequest multipartRequest = (MultipartActionRequest) request;

            for (int index = 0; index < multipartRequest.getFileMap().size(); index++) {
                final MultipartFile file = multipartRequest.getFile("attachment" + index);

                if (!file.isEmpty()) {

                    logDebug("Uploading attachment file: " + file.getOriginalFilename());
                    logDebug("Attachment file size: " + file.getSize());

                    final Calendar cldr = Calendar.getInstance(request.getLocale());
                    cldr.setTime(new Date());
                    final Attachment attachment = Attachment.fromFile(file.getOriginalFilename(), file.getContentType(), cldr, file.getBytes());

                    final String title = multipartRequest.getParameter("attachmentTitle" + index);
                    attachment.setTitle(title);
                    post.getAttachments().add(attachment);
                }

            }
        }
    }

    private void savePost(final ActionRequest request, final BindingResult result, Post post, boolean postIsScheduled, String scheduledDate)
            throws PortletRequestBindingException, JcrRepositoryException {

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        final Calendar cldr = Calendar.getInstance(request.getLocale());

        final DateTimeZone zone = DateTimeZone.forTimeZone(cldr.getTimeZone());
        final DateTime today = new DateTime(zone);

        final DateTimeFormatter fmt = DateTimeFormat.forPattern(PortletPreferencesWrapper.DEFAULT_POST_DATE_FORMAT);
        post.setLastModifiedDate(today.toString(fmt));

        post.setLanguage(request.getLocale().getLanguage());

        if (postIsScheduled) {
            if (StringUtils.isBlank(scheduledDate))
                result.rejectValue("scheduledDate", "invalid.scheduled.post.publish.date");
            else {

                logDebug("Post is scheduled to be published on " + scheduledDate);

                final DateTime dt = DateTime.parse(scheduledDate, fmt);
                post.setScheduledDate(dt.toString(fmt));
                getRepositoryDao().schedulePost(post, pref.getPortletRepositoryRoot());

                ensureRepositoryRootIsScheduled(pref);
            }
        } else {
            post = preparePost(post, request);

            getRepositoryDao().setPost(post);
        }
    }

    @ActionMapping
    protected void handleAction(final ActionRequest request, final ActionResponse response,
            @RequestParam(value = "postIsScheduled", required = false) boolean postIsScheduled,
            @RequestParam(value = "scheduledPostPublishDate", required = false) String scheduledDate, @ModelAttribute Post post, BindingResult result)
            throws Exception {

        logDebug("Received post object");

        getValidator().validate(post, result);

        if (!result.hasErrors()) {

            logDebug("Processing post attachments...");

            processPostAttachments(request, post);

            logDebug("Post: " + post);
            logDebug("Submitting post object");

            savePost(request, result, post, postIsScheduled, scheduledDate);

            if (!result.hasErrors()) {

                logDebug("Clearing render parameters");
                PortletUtils.clearAllRenderParameters(response);

                if (postIsScheduled)
                    response.setRenderParameter(PortletView.RENDER_PARAM_POST_SCHEDULED_SUCCESS, Boolean.TRUE.toString());

                logDebug("Switching to view mode");
            }
        }

        if (result.hasErrors())
            response.setPortletMode(PortletMode.EDIT);
        else
            response.setPortletMode(PortletMode.VIEW);
    }

    private Post preparePost(Post post, final PortletRequest request) {

        if (post == null)
            post = new Post();

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);

        post.setAuthor(request.getUserPrincipal().getName());
        post.setPath(pref.getPortletRepositoryRoot());

        return post;
    }

    @ModelAttribute("post")
    protected Post formBackingPost(final PortletRequest request) {
        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        Post post = getRepositoryDao().getPost(pref.getPortletRepositoryRoot());

        if (post == null) {
            post = preparePost(post, request);
            logDebug("No post exists in repository. Configured blank post for author " + post.getAuthor() + " at " + post.getPath());
        }

        logDebug("Post: " + post);
        return post;
    }

    @RenderMapping
    protected ModelAndView handleRender(final RenderRequest request, final RenderResponse response) throws Exception {
        final Map<String, Object> data = new HashMap<String, Object>();

        System.out.println(request.getParameter("action"));

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        data.put("portletPreferencesWrapper", pref);
        data.put("scheduledRepositoryRoots", ScheduledPostsManager.getInstance().getRepositoryRoots());

        logDebug("Preparing post for edit");

        return new ModelAndView(PortletView.VIEW_EDIT_POST, data);
    }

    @ActionMapping(params = "action=searchRepository")
    protected void handleActionSearchRepository(final ActionRequest actionrequest, final ActionResponse actionresponse) {
        PortletUtils.clearAllRenderParameters(actionresponse);
    }

    @RenderMapping(params = "action=searchRepository")
    protected ModelAndView handleRenderSearchRepository(final RenderRequest request, final RenderResponse response,
            @ModelAttribute("search") final RepositorySearchOptions options) throws Exception {

        logDebug("Executing search");

        final Collection<Post> results = getRepositoryDao().search(options);

        final Map<String, Object> model = new HashMap<String, Object>();

        if (results != null && results.size() > 0) {

            logDebug("Number of search results found: " + results.size());
            model.put("searchResults", results);

            final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
            model.put("portletPreferences", pref);

        } else
            logDebug("No results are found");

        logDebug("Returning search results");
        final ModelAndView modelAndView = new ModelAndView(PortletView.VIEW_SEARCH_RESULTS_VIEW, model);

        return modelAndView;
    }

    @ActionMapping(params = "action=selectPostFromSearch")
    protected void handleActionSelectPostFromSearchResults(final ActionRequest req, final ActionResponse actionresponse) {
        PortletUtils.clearAllRenderParameters(actionresponse);
    }

    @ActionMapping(params = "action=loadScheduledPosts")
    protected void handleActionLoadScheduledPosts(final ActionRequest request, final ActionResponse response) throws Exception {

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        final Collection<Post> results = getRepositoryDao().getScheduledPosts(pref.getPortletRepositoryRoot());

        if (results != null)
            logDebug("Total schedued posts count is " + results.size());
        else
            logDebug("No scheduled posts are available at this time.");
        final LoadScheduledPostsResponse res = new LoadScheduledPostsResponse();
        res.setScheduledPosts(results);
        redirectAjaxResonse(res, request, response);

    }

    @RenderMapping(params = "action=selectPostFromSearch")
    protected ModelAndView handleRenderSelectPostFromSearchResults(final RenderRequest req, final RenderResponse res,
            @ModelAttribute("post") Post post) throws Exception {

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(req);

        logDebug("Selecting post from search " + post);

        post = getRepositoryDao().getPost(post.getPath());
        post.setAuthor(req.getUserPrincipal().getName());

        logDebug("Post: " + post);

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("portletPreferencesWrapper", pref);
        map.put("post", post);

        return new ModelAndView(PortletView.VIEW_EDIT_POST, map);
    }

    @ActionMapping(params = "action=removeScheduledRepositoryRootController")
    protected void handleActionRemoveScheduledRepositoryRoot(final ActionRequest request, final ActionResponse response,
            @RequestParam("path") String rootPath) throws Exception {

        logDebug("Scheduled repository path to remove: " + rootPath);
        ScheduledPostsManager.getInstance().removeRepositoryRoot(rootPath);

        redirectAjaxResonse(null, request, response);
    }

    @ActionMapping(params = "action=publishScheduledPost")
    protected void handleActionPublishScheduledPost(final ActionRequest request, final ActionResponse response, @RequestParam("path") String path)
            throws Exception {

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        final String toNewPath = pref.getPortletRepositoryRoot();

        {
            logDebug("Scheduled post is at " + path);
            logDebug("Moving post to path " + toNewPath);
        }

        final Post post = getRepositoryDao().getPost(path);
        if (post != null) {

            logDebug("Retrieved scheduled post " + post);

            getRepositoryDao().removePost(post.getPath());
            post.setPath(toNewPath);

            post.setRateCount(0);
            post.setRate(0);

            getRepositoryDao().setPost(post);

            ensureRepositoryRootIsScheduled(pref);

            response.setPortletMode(PortletMode.VIEW);
        } else
            throw new PortletException(path + " does not exist");

    }

    @ActionMapping(params = "action=removeScheduledPost")
    protected void handleActionRemoveScheduledPost(final ActionRequest request, final ActionResponse resp, @RequestParam("path") String postPath)
            throws Exception {

        RemoveScheduledPostResponse response = new RemoveScheduledPostResponse();

        if (!StringUtils.isBlank(postPath)) {

            logDebug("Scheduled post path to remove: " + postPath);
            getRepositoryDao().removePost(postPath);
            response.setRemoveSuccessful(true);
            response.setPostPath(postPath);
        }
        redirectAjaxResonse(response, request, resp);
    }

    @ActionMapping(params = "action=removePostAttachment")
    protected void handleActionRemovePostAttachment(final ActionRequest request, final ActionResponse resp,
            @RequestParam("postPath") String postPath, @RequestParam("attachmentPath") String attachmentPath) throws Exception {

        final RemovePostAttachmentResponse response = new RemovePostAttachmentResponse();

        logDebug("Retrieving repository post at " + postPath);
        logDebug("Attachment path to remove: " + attachmentPath);

        final Post post = getRepositoryDao().getPost(postPath);
        if (post != null) {

            logDebug("Retrieved repository post " + post);

            final Iterator<Attachment> it = post.getAttachments().iterator();

            Attachment attachment = null;
            boolean foundAttachment = false;
            while (!foundAttachment && it.hasNext()) {
                attachment = it.next();
                if (attachment.getPath().equals(attachmentPath)) {

                    logDebug("Removing post attachment: " + attachment);
                    it.remove();
                    foundAttachment = true;
                    response.setRemoveSuccessful(foundAttachment);
                }
            }

            if (foundAttachment) {

                logDebug("Saving post");
                getRepositoryDao().setPost(post);

                request.getPortletSession();

                logDebug("Saved post: " + post);
            }
        }

        redirectAjaxResonse(response, request, resp);

    }

    @ActionMapping(params = "action=viewPostAttachment")
    protected void handleActionViewPostAttachment(final ActionRequest request, final ActionResponse response,
            @RequestParam("attachmentPath") String attachmentPath, @RequestParam("postPath") String postPath) throws Exception {

        final Post post = getRepositoryDao().getPost(postPath);
        PortletUtilities.downloadPostAttachment(post, attachmentPath, request, response);
    }
}