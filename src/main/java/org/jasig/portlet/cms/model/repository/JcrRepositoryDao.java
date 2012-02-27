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

package org.jasig.portlet.cms.model.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.springframework.extensions.jcr.JcrCallback;
import org.springframework.extensions.jcr.support.JcrDaoSupport;

public class JcrRepositoryDao extends JcrDaoSupport implements RepositoryDao {
	private static final String	SCHEDULED_POSTS_NODE_NAME	= "scheduledPosts";
	private final Log			logger						= LogFactory.getLog(getClass());
	private JcrPostDao			postDao						= null;

	@Override
	public Post getPost(final String nodeName) {
		final Object post = getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				Post post = null;

				final JcrPostDao dao = getPostDao();

				if (dao.exists(nodeName)) {
					post = dao.get(nodeName);
					if (post.getAuthor() == null)
						post = null;
				}

				if (post != null && logger.isDebugEnabled()) {
					logger.debug("Retrieved post at path " + post.getPath());
					final List<Attachment> attachments = post.getAttachments();
					if (attachments.size() > 0)
						for (final Attachment attachment : attachments)
							if (attachment.getName() == null)
								attachment.setName(attachment.getFileName());
				}
				return post;
			}
		});

		if (post != null)
			return (Post) post;
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Post> getScheduledPosts(final String rootNodeName) {
		final Object list = getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				final JcrPostDao dao = getPostDao();
				return dao.getChildrenAsList(rootNodeName, JcrRepositoryDao.SCHEDULED_POSTS_NODE_NAME);
			}
		});

		if (list != null)
			return (Collection<Post>) list;
		return null;
	}

	@Override
	public void removePost(final String nodeName) {
		getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				final JcrPostDao dao = getPostDao();
				dao.remove(nodeName);
				return null;
			}
		});
	}

	@Override
	public void schedulePost(final Post post, final String nodeName) {
		getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				try {
					final JcrPostDao dao = getPostDao();

					final String schedulePath = JcrRepositoryDao.SCHEDULED_POSTS_NODE_NAME;
					if (logger.isDebugEnabled())
						logger.debug("Scheduled path for post is " + schedulePath);

					post.setPath(schedulePath);

					final Node nd = dao.ensureNodeExists(nodeName);

					if (logger.isDebugEnabled())
						logger.debug("Checking out node " + nd.getPath());

					final VersionManager mgr = session.getWorkspace().getVersionManager();
					mgr.checkout(nd.getPath());

					if (logger.isDebugEnabled())
						logger.debug("Scheduling post...");

					dao.create(nd.getPath(), post);

					if (logger.isDebugEnabled())
						logger.debug("Scheduled post at " + post.getPath() + " to be published on " + post.getScheduledDate());

				} catch (final RepositoryException e) {
					if (logger.isErrorEnabled())
						logger.error(post, e);
					throw new JcrRepositoryException(e);
				}
				return null;
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Post> search(final RepositorySearchOptions options) {
		final Object list = getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				List<Post> list = null;
				try {
					final JcrPostDao dao = getPostDao();
					list = dao.findAll(options);
				} catch (final RepositoryException e) {
					if (logger.isErrorEnabled())
						logger.error(list, e);
					throw new JcrRepositoryException(e);
				}
				return list;
			}
		});

		if (list != null)
			return (Collection<Post>) list;
		return null;
	}

	@Override
	public void setPost(final Post post) {
		getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				try {
					final JcrPostDao dao = getPostDao();

					if (dao.exists(post.getPath())) {
						if (logger.isErrorEnabled())
							logger.debug("Updating post at path: " + post.getPath());

						dao.update(post);
					} else {
						if (logger.isErrorEnabled())
							logger.debug("Creating post at path: " + post.getPath());

						dao.create(getSession().getRootNode().getPath(), post);
					}

				} catch (final RepositoryException e) {
					if (logger.isErrorEnabled())
						logger.error(post, e);
					throw new JcrRepositoryException(e);
				}
				return null;
			}
		});
	}

	public void setPostDao(final JcrPostDao postDao) {
		this.postDao = postDao;
	}

	private JcrPostDao getPostDao() {
		return postDao;
	}
}
