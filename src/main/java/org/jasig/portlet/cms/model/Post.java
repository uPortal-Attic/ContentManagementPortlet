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

public class Post implements Serializable {

	private static final long serialVersionUID = 6392459877566744761L;
	private String _author = null;
	private Date _date = null;
	private String _content = null;
	private List<Attachment> _attachments = null;
	private String _path = null;

	public Post() {
		setAttachments(new ArrayList<Attachment>());
		setDate(new Date());
	}

	public List<Attachment> getAttachments() {
		return _attachments;
	}

	public String getAuthor() {
		return _author;
	}

	public final String getContent() {
		return _content;
	}

	public Date getDate() {
		return _date;
	}

	public String getPath() {
		return _path;
	}

	public void setAttachments(final List<Attachment> attachments) {
		_attachments = attachments;
	}

	public void setAuthor(final String name) {
		_author = name;
	}

	public void setContent(final String content) {
		_content = content;
	}

	public void setDate(final Date dateTime) {
		_date = dateTime;
	}

	public void setPath(final String loc) {
		_path = loc;
	}
}
