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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.cms.model.repository.schedule.ScheduledPostsManager;
import org.jasig.portlet.cms.view.PortletView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping("CONFIG")
public class ConfigController extends AbstractPortletController {

    @ActionMapping
    protected void handleAction(final ActionRequest request, final ActionResponse response) throws Exception {
        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        final Object[] keys = pref.getKeys();

        final Map<String, ?> params = request.getParameterMap();

        ScheduledPostsManager.getInstance().removeRepositoryRoot(pref.getPortletRepositoryRoot());

        for (final Object keyObj : keys) {

            String key = keyObj.toString();

            final String value = request.getParameter(key);
        
            if (params.containsKey(key.toString())) 
                pref.setProperty(key.toString(), value);
            else if (value == null)
                pref.setProperty(key, false);
        }

        logDebug("Saving portlet preferences...");

        pref.save();

        ScheduledPostsManager.getInstance().addRepositoryRoot(pref.getPortletRepositoryRoot());
        response.setPortletMode(PortletMode.VIEW);

    }

    @RenderMapping
    protected ModelAndView handleRender(final RenderRequest request, final RenderResponse response) throws Exception {

        final Map<String, Object> model = new HashMap<String, Object>();
        final PortletPreferencesWrapper pref = new PortletPreferencesWrapper(request);
        model.put("portletPreferences", pref);

        return new ModelAndView(PortletView.VIEW_CONFIG_PORTLET, model);
    }

}
