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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.PortletConfiguration;
import org.jasig.portlet.cms.view.PortletView;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;


public class ConfigController extends AbstractController {
	
	private final Log	logger	= LogFactory.getLog(getClass());
	
	@Override
	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		PortletConfiguration pref = new PortletConfiguration();
		Iterator<?> it = pref.getKeys();
		
		Map<?, ?> params = request.getParameterMap();
		
		while (it.hasNext()) {
			String key = it.next().toString();
			
			if (params.containsKey(key)) {
				String value = request.getParameter(key);
				pref.setProperty(key, value);
			}
		}
		
		response.setPortletMode(PortletMode.VIEW);
	}
	
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		
		final Map<String, Object> model = new HashMap<String, Object>();
		PortletConfiguration pref = new PortletConfiguration();
		model.put("portletPreferences", pref);
		
		return new ModelAndView(PortletView.CONFIG_PORTLET, model);
	}
	
}
