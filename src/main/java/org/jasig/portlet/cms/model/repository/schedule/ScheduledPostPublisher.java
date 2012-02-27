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

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.controller.PortletPreferencesWrapper;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.JcrRepositoryException;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ScheduledPostPublisher {

	private final Log		logger			= LogFactory.getLog(getClass());

	private RepositoryDao	repositoryDao	= null;

	public void execute() {
		try {
			final ScheduledPostsManager mgr = ScheduledPostsManager.getInstance();
			if (mgr.size() > 0) {
				final Iterator<String> it = mgr.getRepositoryRoots();
				if (logger.isDebugEnabled())
					logger.debug("Scheduled posts manager is processing repository roots...");

				while (it.hasNext())
					publishScheduledPostsForRepositoryRoot(it.next());
			} else if (logger.isDebugEnabled())
				logger.debug("No repository roots are available for scheduled posts manager.");

		} catch (final Exception e) {
			if (logger.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

	}

	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	private void publishScheduledPostsForRepositoryRoot(final String root) throws JcrRepositoryException {
		final Collection<Post> scheduledPosts = getRepositoryDao().getScheduledPosts(root);

		if (scheduledPosts != null && scheduledPosts.size() > 0)
			for (final Post post : scheduledPosts) {

				final Calendar cldr = Calendar.getInstance(post.getLocale());
				final DateTimeFormatter fmt = DateTimeFormat
						.forPattern(PortletPreferencesWrapper.DEFAULT_POST_DATE_FORMAT);

				final DateTime dt = DateTime.parse(post.getScheduledDate(), fmt);

				final DateTimeZone zone = DateTimeZone.forTimeZone(cldr.getTimeZone());
				final DateTime today = new DateTime(zone);

				if (dt.isEqual(today) || dt.isBefore(today)) {
					if (logger.isDebugEnabled())
						logger.debug("Scheduled for today " + post.getPath() + " at " + post.getScheduledDate());

					getRepositoryDao().removePost(post.getPath());
					post.setPath(root);
					post.setRateCount(0);
					post.setRate(0);
					getRepositoryDao().setPost(post);

					if (logger.isDebugEnabled())
						logger.debug("Published scheduled post " + post.getPath() + " at "
								+ post.getScheduledDate() + " to " + post.getPath());
				}
			}
		else
			ScheduledPostsManager.getInstance().removeRepositoryRoot(root);
	}


	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}
}