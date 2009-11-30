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
package org.jasig.portlet.cms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcrom.annotations.JcrFileNode;
import org.jcrom.annotations.JcrName;
import org.jcrom.annotations.JcrNode;
import org.jcrom.annotations.JcrPath;
import org.jcrom.annotations.JcrProperty;

@JcrNode(mixinTypes = { "mix:versionable" })
public class Post implements Serializable {

	private static final long serialVersionUID = 6392459877566744761L;

	@JcrProperty
	private String author = null;
	@JcrProperty
	private Date date = null;
	@JcrProperty
	private String content = null;
	@JcrFileNode
	private List<Attachment> attachments = null;
	@JcrName
	private String name;
	@JcrPath
	private String path = null;

	public Post() {
		setAttachments(new ArrayList<Attachment>());
		setDate(new Date());
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public String getAuthor() {
		return author;
	}

	public final String getContent() {
		return content;
	}

	public Date getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public void setAttachments(final List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setAuthor(final String name) {
		author = name;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public void setDate(final Date dateTime) {
		date = dateTime;
	}

	public void setPath(final String loc) {
		path = loc;
		name = loc;
	}
}
