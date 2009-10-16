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
package org.jasig.portlet.cms.model.repository.jcr.jackrabbit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositoryDao;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.springmodules.jcr.jackrabbit.ocm.JcrMappingTemplate;

public class JcrRepositoryDao implements RepositoryDao {

	private JcrMappingTemplate _template;

	@Override
	public Post getPost(final String nodeName) {
		final Object post = getTemplate().getObject(nodeName);
		if (post != null)
			return (Post) post;
		return null;
	}

	@Override
	public List<Post> search(final RepositorySearchOptions options) throws Exception {
		List<Post> list = null;

		final String queryString = "SELECT * FROM nt:base WHERE contains (content, '{0}')";
		final QueryManager manager = getTemplate().getSessionFactory().getSession().getWorkspace()
		        .getQueryManager();
		final Query query = manager.createQuery(MessageFormat.format(queryString, options.getKeywords()),
		        Query.SQL);
		final QueryResult results = query.execute();
		final NodeIterator iterator = results.getNodes();

		if (iterator.hasNext()) {
			list = new ArrayList<Post>();
			while (iterator.hasNext()) {
				final Node node = (Node) iterator.next();
				final Post post = getPost(node.getPath());
				if (post != null)
					list.add(post);
			}
		}

		return list;

	}

	@Override
	public void setPost(final Post post) {
		if (getPost(post.getPath()) != null)
			getTemplate().update(post);
		else
			getTemplate().insert(post);
		getTemplate().save();
	}

	public void setTemplate(final JcrMappingTemplate t) {
		_template = t;
	}

	private JcrMappingTemplate getTemplate() {
		return _template;
	}
}
