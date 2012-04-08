/**
 * Licensed to Jasig under one or more contributor license agreements. See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. Jasig licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jasig.portlet.cms.controller;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jasig.portlet.cms.controller.attachment.AbstractAttachmentThumbnailCreator;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.UpdatePostRatingResponse;
import org.jasig.portlet.cms.util.PortletUtilities;
import org.jasig.portlet.cms.view.PortletView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller
@RequestMapping(value = "VIEW")
public class ViewPostController extends AbstractPortletController {

    @ActionMapping
    protected void handleAction(ActionRequest req, ActionResponse res) {
    }

    @RenderMapping
    protected ModelAndView handleRender(final RenderRequest request, final RenderResponse response) throws Exception {

        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);

        ensureRepositoryRootIsScheduled(pref);

        logDebug("Retrieving repository post");

        final Post post = getRepositoryDao().getPost(pref.getPortletRepositoryRoot());

        final Map<String, Object> map = new HashMap<String, Object>();

        map.put("post", post);
        map.put("isAuthorRole", request.isUserInRole("author"));
        map.put("portletPreferences", pref);

        final Boolean postScheduledSuccess = PortletRequestUtils.getBooleanParameter(request, PortletView.RENDER_PARAM_POST_SCHEDULED_SUCCESS,
                Boolean.FALSE);
        map.put(PortletView.RENDER_PARAM_POST_SCHEDULED_SUCCESS, postScheduledSuccess);

        logDebug("Returning repository post " + post);

        return new ModelAndView(PortletView.VIEW_POST_VIEW, map);

    }

    @ActionMapping(params = "action=updatePostRating")
    protected void handleActionUpdatePostRating(final ActionRequest request, final ActionResponse resp, @RequestParam("postPath") String postPath,
            @RequestParam int rateValue) throws Exception {

        UpdatePostRatingResponse response = new UpdatePostRatingResponse();

        logDebug("Retrieving repository post at " + postPath);
        logDebug("Post rate value is " + rateValue);

        final Post post = getRepositoryDao().getPost(postPath);
        if (post != null) {

            if (post.getRate() == 0)
                post.setRate(rateValue);
            else
                post.setRate((post.getRate() + rateValue) / 2);

            post.setRateCount(post.getRateCount() + 1);

            getRepositoryDao().setPost(post);
            response.setUpdateSuccessful(true);
        }

        response.setPost(post);

        redirectAjaxResonse(response, request, resp);
    }

    @ActionMapping(params = "action=viewPostAttachment")
    protected void handleActionViewPostAttachment(final ActionRequest request, final ActionResponse response,
            @RequestParam("attachmentPath") String attachmentPath, @RequestParam("postPath") String postPath) throws Exception {

        final Post post = getRepositoryDao().getPost(postPath);
        PortletUtilities.downloadPostAttachment(post, attachmentPath, request, response);
    }

    @ResourceMapping
    public void handleResourceRequest(final ResourceRequest request, final ResourceResponse response) throws Exception {

        final String postPath = PortletRequestUtils.getRequiredStringParameter(request, "postPath");
        final Writer writer = response.getWriter();

        logDebug("Retrieving post to process attachments...");

        final Post post = getRepositoryDao().getPost(postPath);
        if (post != null) {

            logDebug("Retrieved post to process attachments...");

            final List<Attachment> list = post.getAttachments();
            final StringBuilder builder = new StringBuilder();

            for (final Attachment attachment : list) {
                final AbstractAttachmentThumbnailCreator creator = AbstractAttachmentThumbnailCreator.createImageThumbnailCreator(attachment,
                        request, getPortletContext(), response);

                builder.append(creator.generateHtmlFragment());
            }

            if (builder.length() > 0) {

                logDebug("Writing output response data...");

                response.setContentType("text/html");
                writer.write(builder.toString());
                writer.flush();
            }
        }
    }

}
