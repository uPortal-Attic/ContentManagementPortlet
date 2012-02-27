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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.jcrom.Jcrom;
import org.jcrom.dao.AbstractJcrDAO;
import org.springframework.extensions.jcr.SessionFactory;

public class JcrPostDao extends AbstractJcrDAO<Post> {

	private final Log	logger	= LogFactory.getLog(getClass());

	public JcrPostDao(final Session session, final Jcrom jcrom) {
		super(Post.class, session, jcrom);
	}

	public JcrPostDao(final SessionFactory factory, final Jcrom jcrom) throws RepositoryException {
		super(Post.class, factory.getSession(), jcrom);
	}

	public JcrPostDao(final SessionFactory factory, final Jcrom jcrom, final String[] mixinType) throws RepositoryException {
		super(Post.class, factory.getSession(), jcrom, mixinType);
	}

	@Override
	public Post create(final Post entity) {
		return super.create(entity);
	}

	@Override
	public Post create(final String parentNodePath, final Post entity) {
		return super.create(parentNodePath, entity);
	}

	public Node ensureNodeExists(final String nodeName) throws RepositoryException {
		Node nd = null;
		if (!exists(nodeName)) {
			if (logger.isDebugEnabled())
				logger.debug("Creating node " + nodeName);
			nd = getSession().getRootNode().addNode(nodeName);

			if (logger.isDebugEnabled())
				logger.debug("Created node " + nd.getPath());

			if (logger.isDebugEnabled())
				logger.debug("Mixin types for node are " + Arrays.toString(getMixinTypes()));

			for (final String type : getMixinTypes()) {
				if (logger.isDebugEnabled())
					logger.debug("Adding mixin type " + type + " to node " + nodeName);
				nd.addMixin(type);
				if (logger.isDebugEnabled())
					logger.debug("Added mixin type " + type + " to node " + nodeName);
			}
			if (getSession().hasPendingChanges())
				getSession().save();
		}
		else
			nd = getSession().getRootNode().getNode(nodeName);
		return nd;
	}

	@Override
	public boolean exists(final String arg0) {
		return super.exists(arg0);
	}

	public List<Post> findAll(final RepositorySearchOptions options) throws RepositoryException {
		final String keyword = StringEscapeUtils.escapeHtml(options.getKeyword());
		List<Post> list = Collections.emptyList();


		if (!StringUtils.isBlank(keyword)) {
			final String searchQuery = "//element(*,mix:versionable)[jcr:like(@content, '%" + keyword + "%')]";
			if (logger.isDebugEnabled())
				logger.debug("Search query generated: " + searchQuery);
			list = findByXPath(searchQuery, "*", -1);
		}

		return list;
	}

	public List<Post> findByXPath(final String xpath) {
		return super.findByXPath(xpath, "*", -1);
	}

	@Override
	public List<Post> findByXPath(final String xpath, final String childNameFilter, final int maxDepth) {
		return super.findByXPath(xpath, childNameFilter, maxDepth);
	}

	@Override
	public Post get(final String path) {
		return super.get(path);
	}

	public List<Post> getChildrenAsList(final String rootNodeName, final String childNodeName) throws RepositoryException {
		List<Post> list = null;
		try {
			final Node nd = getSession().getRootNode().getNode(rootNodeName);
			final NodeIterator it = nd.getNodes(childNodeName);
			list = toList(it, "*", -1);
		} catch (final PathNotFoundException e) {

		}
		return list;
	}

	public Jcrom getJcrom() {
		return jcrom;
	}

	public String[] getMixinTypes () {
		return mixinTypes;
	}

	@Override
	public void move(final Post arg0, final String arg1) {
		super.move(arg0, arg1);
	}

	@Override
	public String update(final Post entity) {
		return super.update(entity);
	}

	@Override
	public String update(final Post arg0, final String arg1, final int arg2) {
		return super.update(arg0, arg1, arg2);
	}

	@Override
	protected List<Post> toList(final NodeIterator nodeIterator, final String childNameFilter, final int maxDepth) {
		return super.toList(nodeIterator, childNameFilter, maxDepth);
	}

	@Override
	protected List<Post> toList(final NodeIterator nodeIterator, final String childNameFilter, final int maxDepth, final long resultSize) {
		return super.toList(nodeIterator, childNameFilter, maxDepth, resultSize);
	}
}
