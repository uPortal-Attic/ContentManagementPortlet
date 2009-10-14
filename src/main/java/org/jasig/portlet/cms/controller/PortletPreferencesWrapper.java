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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

public final class PortletPreferencesWrapper {
	private PortletPreferences _prefences = null;
	private PortletRequest _portletRequest = null;

	private static final String DEFAULT_REPOSITORY_ROOT_NAME = "root";

	public PortletPreferencesWrapper(final PortletRequest request) {
		_portletRequest = request;
		_prefences = _portletRequest.getPreferences();
	}

	public String getPortletRepositoryRoot() {
		String result = _prefences.getValue("repository.root",
		        PortletPreferencesWrapper.DEFAULT_REPOSITORY_ROOT_NAME);
		if (result == null || result.trim().length() <= 0)
			result = PortletPreferencesWrapper.DEFAULT_REPOSITORY_ROOT_NAME;
		return result;
	}

	public String getPortletUserName() {
		return _portletRequest.getUserPrincipal().getName();
	}
}
