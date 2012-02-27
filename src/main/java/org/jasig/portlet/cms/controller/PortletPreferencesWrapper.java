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

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class PortletPreferencesWrapper {
	public static final String		DEFAULT_POST_DATE_FORMAT		= "MM/dd/yyyy hh:mm a";

	private static final Boolean	DEFAULT_ATTACHMENT_THUMBNAILS_ENABLED	= Boolean.TRUE;
	private static final Long		DEFAULT_MAX_POST_ATTACHMENTS	= 2L;
	private static final String		DEFAULT_REPOSITORY_ROOT_NAME	= "root";
	private static final Boolean	DEFAULT_XSS_VALIDATION_ENABLED	= Boolean.TRUE;

	private static final String		PREF_KEY_ATTACHMENT_THUMBNAILS_ENABLED	= "attachment.thumbnails.enabled";
	private static final String		PREF_KEY_POST_ATTACHMENTS_MAX	= "post.attachments.max";
	private static final String		PREF_KEY_REPOSITORY_ROOT		= "repository.root";
	private static final String		PREF_KEY_XSS_VALIDATION_ENABLED	= "xss.validation.enabled";

	private final Log				logger							= LogFactory.getLog(getClass());
	private PortletRequest			portletRequest					= null;

	private PortletPreferences		preferences								= null;



	public PortletPreferencesWrapper(final PortletRequest request) {
		portletRequest = request;
		preferences = portletRequest.getPreferences();

		checkForPreferencesKeys();
	}

	public Object[] getKeys() {
		return preferences.getMap().keySet().toArray();
	}

	public long getMaximumPostAttachments() {
		final String value = getProperty(PREF_KEY_POST_ATTACHMENTS_MAX, DEFAULT_MAX_POST_ATTACHMENTS.toString());
		return Long.valueOf(value);
	}

	public String getPortletRepositoryRoot() {
		final String result = getProperty(PREF_KEY_REPOSITORY_ROOT, DEFAULT_REPOSITORY_ROOT_NAME);
		return result;
	}

	public String getPortletUserName() {
		return portletRequest.getUserPrincipal().getName();
	}

	public String getProperty(final String key) {
		return getProperty(key, null);
	}

	public boolean isAttachmentThumbnailsEnabled() {
		final String value = getProperty(PREF_KEY_ATTACHMENT_THUMBNAILS_ENABLED, DEFAULT_ATTACHMENT_THUMBNAILS_ENABLED.toString());
		return Boolean.valueOf(value);
	}

	public boolean isXssValidationEnabled() {
		final String value = getProperty(PREF_KEY_XSS_VALIDATION_ENABLED, DEFAULT_XSS_VALIDATION_ENABLED.toString());
		return Boolean.valueOf(value);
	}

	public void setAttachmentThumbnailsEnabled(final boolean val) {
		setProperty(PREF_KEY_ATTACHMENT_THUMBNAILS_ENABLED, val);
	}

	public void setMaximumPostAttachments(final Long val) {
		setProperty(PREF_KEY_POST_ATTACHMENTS_MAX, val);
	}

	public void setPortletRepositoryRoot(final String val) {
		setProperty(PREF_KEY_REPOSITORY_ROOT, PortletPreferencesWrapper.DEFAULT_REPOSITORY_ROOT_NAME);
	}

	public void setXssValidationEnabled(final boolean val) {
		setProperty(PREF_KEY_XSS_VALIDATION_ENABLED, val);
	}

	private void checkForPreferencesKeys() {
		final Map<?, ?> map = preferences.getMap();

		if (!map.containsKey(PREF_KEY_POST_ATTACHMENTS_MAX))
			setMaximumPostAttachments(DEFAULT_MAX_POST_ATTACHMENTS);

		if (!map.containsKey(PREF_KEY_REPOSITORY_ROOT))
			setPortletRepositoryRoot(DEFAULT_REPOSITORY_ROOT_NAME);

		if (!map.containsKey(PREF_KEY_XSS_VALIDATION_ENABLED))
			setXssValidationEnabled(DEFAULT_XSS_VALIDATION_ENABLED);
	}

	String getProperty(final String key, final String def) {
		return preferences.getValue(key, def);
	}

	void save() {
		try {
			preferences.store();
		} catch (final Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e);
		}
	}

	void setProperty(final String key, final Object value) {
		try {
			if (!preferences.isReadOnly(key))
				preferences.setValue(key, value.toString());
		} catch (final ReadOnlyException e) {
			if (logger.isErrorEnabled())
				logger.error(e);
		}
	}
}
