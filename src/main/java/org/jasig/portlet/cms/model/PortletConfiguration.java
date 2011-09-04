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
package org.jasig.portlet.cms.model;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PortletConfiguration {
	
	private final Log				logger								= LogFactory.getLog(getClass());
	private PropertiesConfiguration	config								= null;
	private static String			portletConfigurationFile			= null;
	
	private static final String		DEFAULT_REPOSITORY_ROOT_NAME		= "root";
	private static final long		DEFAULT_MAX_POST_ATTACHMENTS		= 2;
	private static final boolean	DEFAULT_XSS_VALIDATION_ENABLED		= Boolean.TRUE;
	private static final String		DEFAULT_SCHEDULED_POST_DATE_FORMAT	= "MM/dd/yyyy hh:mm";
	
	static {
		PortletConfiguration.portletConfigurationFile = "portlet" + RandomUtils.nextLong() + ".properties";
	}
	
	public PortletConfiguration() {
		try {
			File propFile = new File(PortletConfiguration.portletConfigurationFile);
			if (!propFile.exists())
				propFile.createNewFile();
			
			config = new PropertiesConfiguration(propFile);
			
			config.setAutoSave(true);
			createMissingConfigurationKeys();
			
		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error(config, e);
		}
	}
	
	private void createMissingConfigurationKeys() {
		if (!config.containsKey("repository.root")) {
			config.addProperty("repository.root", PortletConfiguration.DEFAULT_REPOSITORY_ROOT_NAME);
			if (logger.isDebugEnabled())
				logger.debug("Created missing portlet key repository.root");
		}
		
		if (!config.containsKey("post.attachments.max")) {
			config.addProperty("post.attachments.max", PortletConfiguration.DEFAULT_MAX_POST_ATTACHMENTS);
			if (logger.isDebugEnabled())
				logger.debug("Created missing portlet key post.attachments.max");
		}
		
		if (!config.containsKey("xss.validation.enabled")) {
			config.addProperty("xss.validation.enabled", PortletConfiguration.DEFAULT_XSS_VALIDATION_ENABLED);
			if (logger.isDebugEnabled())
				logger.debug("Created missing portlet key xss.validation.enabled");
		}
		
	}
	
	public Iterator<?> getKeys() {
		return config.getKeys();
	}
	
	public long getMaximumPostAttachments() {
		return config.getLong("post.attachments.max", PortletConfiguration.DEFAULT_MAX_POST_ATTACHMENTS);
	}
	
	public String getPortletRepositoryRoot() {
		return config.getString("repository.root", PortletConfiguration.DEFAULT_REPOSITORY_ROOT_NAME);
	}
	
	public Object getProperty(String key) {
		return config.getProperty(key);
	}
	
	public String getScheduledPostDateFormat() {
		return PortletConfiguration.DEFAULT_SCHEDULED_POST_DATE_FORMAT;
	}
	
	public boolean isXssValidationEnabled() {
		return config.getBoolean("xss.validation.enabled", PortletConfiguration.DEFAULT_XSS_VALIDATION_ENABLED);
	}
	
	public void setMaximumPostAttachments(long val) {
		config.setProperty("post.attachments.max", val);
	}
	
	public void setPortletRepositoryRoot(String val) {
		config.setProperty("repository.root", PortletConfiguration.DEFAULT_REPOSITORY_ROOT_NAME);
	}
	
	public void setProperty(String key, Object value) {
		config.setProperty(key, value);
	}
	
	public void setXssValidationEnabled(boolean val) {
		config.setProperty("xss.validation.enabled", val);
	}
	
}
