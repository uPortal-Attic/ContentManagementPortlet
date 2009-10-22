/*
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 **/
package org.jasig.portlet.cms.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.view.PortletView;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractCommandController;
import org.springframework.web.portlet.util.PortletUtils;

public class SelectPostFromSearchController extends AbstractCommandController {
	private final Log _logger = LogFactory.getLog(getClass());

	@Override
	protected void handleAction(final ActionRequest arg0, final ActionResponse actionresponse,
	        final Object arg2, final BindException arg3) throws Exception {
		PortletUtils.clearAllRenderParameters(actionresponse);
	}

	@Override
	protected ModelAndView handleRender(final RenderRequest arg0, final RenderResponse arg1,
	        final Object arg2, final BindException arg3) throws Exception {

		final Post post = (Post) arg2;
		final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(arg0);

		_logger.debug("Selecting post from search");

		post.setAuthor(pref.getPortletUserName());
		post.setDate(new Date());

		_logger.debug("Post is at " + post.getPath());
		_logger.debug("Post content is " + post.getContent());
		_logger.debug("Post author is " + post.getAuthor());
		_logger.debug("Post date is " + post.getDate());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("post", post);

		return new ModelAndView(PortletView.EDIT_POST, map);
	}

}
