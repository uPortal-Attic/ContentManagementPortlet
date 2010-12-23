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
package org.jasig.portlet.cms.model.repository;

import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.jcrom.Jcrom;
import org.jcrom.dao.AbstractJcrDAO;
import org.springframework.extensions.jcr.SessionFactory;

public class JcrPostDao extends AbstractJcrDAO<Post> {

	public JcrPostDao(final Session session, final Jcrom jcrom) {
		super(Post.class, session, jcrom);
	}

	public JcrPostDao(final SessionFactory factory, final Jcrom jcrom) throws RepositoryException {
		super(Post.class, factory.getSession(), jcrom);
	}

	@Override
	public Post create(final Post entity) {
		return super.create(entity);
	}

	@Override
	public Post create(final String arg0, final Post arg1) {
		return super.create(arg0, arg1);
	}

	@Override
	public boolean exists(final String arg0) {
		return super.exists(arg0);
	}

	public List<Post> findAll(final RepositorySearchOptions options) throws RepositoryException {
		final String keyword = StringEscapeUtils.escapeHtml(options.getKeyword());
		List<Post> list = Collections.EMPTY_LIST;

		if (!StringUtils.isBlank(keyword)) {
			final Node rootNode = getSession().getRootNode();
			list = findByXPath("/" + rootNode.getPath()
					+ "element(*,mix:versionable)[jcr:like(@content, '%" + keyword + "%')]", "*", -1);
		}
		return list;

	}

	@Override
	public Post get(final String path) {
		return super.get(path);
	}

	@Override
	public String update(final Post entity) {
		return super.update(entity);
	}

	@Override
	public String update(final Post arg0, final String arg1, final int arg2) {
		return super.update(arg0, arg1, arg2);
	}


}
