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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.JcrRepositoryException;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.jasig.portlet.cms.model.repository.schedule.ScheduledPostsManager;
import org.jasig.web.service.AjaxPortletSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.portlet.context.PortletContextAware;

public abstract class AbstractPortletController implements PortletContextAware {
    private final Log          logger             = LogFactory.getLog(getClass());

    private RepositoryDao      repositoryDao      = null;

    private AjaxPortletSupport ajaxPortletSupport = null;

    private PortletContext     portletContext     = null;

    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

    protected PortletContext getPortletContext() {
        return this.portletContext;
    }

    protected AjaxPortletSupport getAjaxPortletSupport() {
        return this.ajaxPortletSupport;
    }

    @Autowired
    @Qualifier("ajaxPortletSupportService")
    public void setAjaxPortletSupport(AjaxPortletSupport ajaxPortletSupport) {
        this.ajaxPortletSupport = ajaxPortletSupport;
    }

    @Autowired
    @Qualifier("repositoryDao")
    protected void setRepositoryDao(final RepositoryDao repositoryDao) {
        this.repositoryDao = repositoryDao;
    }

    protected RepositoryDao getRepositoryDao() {
        return repositoryDao;
    }

    private Log getLogger() {
        return logger;
    }

    protected void logDebug(Object debug) {
        if (getLogger().isDebugEnabled())
            getLogger().debug(debug);
    }

    protected void logDebug(Object debug, Throwable throwable) {
        if (getLogger().isDebugEnabled())
            getLogger().debug(debug, throwable);
    }

    protected void redirectAjaxResonse(Object response, ActionRequest request, ActionResponse resp) throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("response", response);

        getAjaxPortletSupport().redirectAjaxResponse(model, request, resp);
    }

    protected void ensureRepositoryRootIsScheduled(final PortletPreferencesWrapper pref) throws JcrRepositoryException {
        if (!ScheduledPostsManager.getInstance().containsRoot(pref.getPortletRepositoryRoot())) {
            final Collection<Post> col = getRepositoryDao().getScheduledPosts(pref.getPortletRepositoryRoot());
            if (col != null && col.size() > 0)
                ScheduledPostsManager.getInstance().addRepositoryRoot(pref.getPortletRepositoryRoot());
        }
    }
}
