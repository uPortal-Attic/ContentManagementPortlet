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
package org.jasig.portlet.cms.model.repository.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.PortletConfiguration;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;

public class ScheduledPostPublisher {
	
	private final Log		logger			= LogFactory.getLog(getClass());
	
	private RepositoryDao	repositoryDao	= null;
	
	public void execute() {
		try {
			PortletConfiguration config = new PortletConfiguration();
			
			Collection<Post> scheduledPosts = getRepositoryDao().getScheduledPosts(config.getPortletRepositoryRoot());
			
			if (scheduledPosts != null)
				for (Post post : scheduledPosts) {
					
					DateFormat fmt = new SimpleDateFormat(config.getScheduledPostDateFormat(), post.getLocale());
					Calendar cldr = Calendar.getInstance(post.getLocale());
					fmt.setCalendar(cldr);
					Date dt = fmt.parse(post.getScheduledDate());
					
					Date today = fmt.parse(fmt.format(Calendar.getInstance().getTime()));
					
					if (dt.compareTo(today) <= 0) {
						if (logger.isDebugEnabled())
							logger.debug("Scheduled for today " + post.getPath() + " at " + post.getScheduledDate());
						
						geRepositoryDao().removePost(post.getPath());
						post.setPath(config.getPortletRepositoryRoot());
						post.setRateCount(0);
						post.setRate(0);
						geRepositoryDao().setPost(post);
						
						if (logger.isDebugEnabled())
							logger.debug("Published scheduled post " + post.getPath() + " at " + post.getScheduledDate() + " to "
									+ config.getPortletRepositoryRoot());
					}
				}
			
		} catch (Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
		
	}
	
	private RepositoryDao geRepositoryDao() {
		return repositoryDao;
	}
	
	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}
	
	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}
}
