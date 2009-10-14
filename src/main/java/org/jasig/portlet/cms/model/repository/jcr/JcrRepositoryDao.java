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
package org.jasig.portlet.cms.model.repository.jcr;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.RepositoryDao;
import org.jasig.portlet.cms.model.RepositorySearchOptions;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.support.JcrDaoSupport;

public class JcrRepositoryDao extends JcrDaoSupport implements RepositoryDao {
	@Override
	public Post getPost(final String nodeName) {
		final Post post = (Post) getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				final Node node = getNode(session, nodeName);
				return getPost(node);
			}
		});
		return post;
	}

	@Override
	public List<Post> search(final RepositorySearchOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPost(final Post post) {
		getTemplate().execute(new JcrCallback() {
			@Override
			public Object doInJcr(final Session session) throws IOException, RepositoryException {
				if (post.getContent() != null && post.getContent().trim().length() > 0) {
					final Node node = getNode(session, post.getLocation());
					node.setProperty("author", post.getAuthorName());
					final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
					node.setProperty("postedDateTime", formatter.format(post.getPostedDateTime()));
					node.setProperty("content", post.getContent());
					session.save();
				}
				return null;
			}
		});

	}

	private Node getNode(final Session session, final String nodeName) throws RepositoryException {

		final Node rootNode = session.getRootNode();
		Node node = null;
		if (rootNode.hasNode(nodeName))
			node = rootNode.getNode(nodeName);
		else
			node = rootNode.addNode(nodeName);
		return node;
	}

	private Post getPost(final Node node) throws RepositoryException {
		Post post = null;

		if (node.hasProperty("author") && node.hasProperty("postedDateTime") && node.hasProperty("content")) {
			final String author = node.getProperty("author").getValue().getString();

			final DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
			Date postedDateTime = null;
			try {
				postedDateTime = formatter.parse(node.getProperty("postedDateTime").getValue().getString());
			} catch (final ParseException e) {
				throw new RepositoryException(e.getMessage(), e);
			}

			final String content = node.getProperty("content").getValue().getString();
			post = new Post();
			post.setAuthorName(author);
			post.setPostedDateTime(postedDateTime);
			post.setContent(content);
			post.setLocation(node.getName());
		}

		return post;
	}

}
