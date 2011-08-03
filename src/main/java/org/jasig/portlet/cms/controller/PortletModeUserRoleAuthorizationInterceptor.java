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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSecurityException;

import org.springframework.web.portlet.handler.HandlerInterceptorAdapter;

public class PortletModeUserRoleAuthorizationInterceptor extends HandlerInterceptorAdapter {
	
	private Map<String, String> authorizedRolesMap = null;
	
	public PortletModeUserRoleAuthorizationInterceptor() {
	}
	
	@Override
	public boolean preHandle(final PortletRequest request, final PortletResponse response,
			final Object handler) throws PortletException, IOException {
		
		boolean allowed = false;
		if (authorizedRolesMap != null) {
			final Iterator<Map.Entry<String, String>> it = authorizedRolesMap.entrySet().iterator();
			while (!allowed && it.hasNext()) {
				final Map.Entry<String, String> entry = it.next();
				final PortletMode mode = new PortletMode(entry.getKey());
				String role = null;
				
				if (entry.getValue() != null)
					role = entry.getValue().toString();
				
				if (request.getPortletMode().equals(mode))
					allowed = role.length() <= 0 || role.equalsIgnoreCase("*") || request.isUserInRole(role);
			}
		} else
			allowed = true;
		
		if (!allowed)
			throw new PortletSecurityException("Request not authorized");
		return allowed;
	}
	
	public final void setAuthorizedRolesMap(final Map<String, String> authorizedRolesMap) {
		this.authorizedRolesMap = authorizedRolesMap;
	}
}
