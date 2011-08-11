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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

public final class PortletPreferencesWrapper {
	private PortletPreferences preferences = null;
	private PortletRequest portletRequest = null;
	
	private static final String	DEFAULT_REPOSITORY_ROOT_NAME	= "root";
	private static final long	DEFAULT_MAX_POST_ATTACHMENTS	= 2;
	
	public PortletPreferencesWrapper(final PortletRequest request) {
		portletRequest = request;
		preferences = portletRequest.getPreferences();
	}
	
	public long getMaximumPostAttachments() {
		final String value = preferences.getValue("post.attachments.max", "2");
		if (value == null || value.trim().length() <= 0)
			return PortletPreferencesWrapper.DEFAULT_MAX_POST_ATTACHMENTS;
		return Long.valueOf(value);
	}
	
	public String getPortletRepositoryRoot() {
		String result = preferences.getValue("repository.root",
				PortletPreferencesWrapper.DEFAULT_REPOSITORY_ROOT_NAME);
		if (result == null || result.trim().length() <= 0)
			result = PortletPreferencesWrapper.DEFAULT_REPOSITORY_ROOT_NAME;
		return result;
	}
	
	public String getPortletUserName() {
		return portletRequest.getUserPrincipal().getName();
	}
	
	public boolean isXssValidationEnabled() {
		final String value = preferences.getValue("xss.validation.enabled", Boolean.TRUE.toString());
		if (value == null || value.trim().length() <= 0)
			return true;
		
		return Boolean.valueOf(value);
	}
}
