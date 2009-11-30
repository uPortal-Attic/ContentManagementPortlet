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

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringEscapeUtils;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.jcrom.Jcrom;
import org.jcrom.dao.AbstractJcrDAO;

public class PostDao extends AbstractJcrDAO<Post> {

	public PostDao(final Session session, final Jcrom jcrom) {
		super(Post.class, session, jcrom);
	}

	public List<Post> findAll(final RepositorySearchOptions options) throws RepositoryException {
		final String keyword = StringEscapeUtils.escapeHtml(options.getKeyword());

		final Node rootNode = getSession().getRootNode();
		final List<Post> list = findByXPath("/" + rootNode.getPath()
		        + "element(*,mix:versionable)[jcr:like(@content, '%" + keyword + "%')]", "*", -1);
		return list;

	}

}
