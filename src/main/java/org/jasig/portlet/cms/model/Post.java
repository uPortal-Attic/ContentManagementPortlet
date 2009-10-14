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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

	private String _authorName = null;
	private Date _postedDateTime = null;
	private String _content = null;

	private List<Attachment> _attachments = null;

	private String _location = null;

	public Post() {
		_attachments = new ArrayList<Attachment>();
	}

	public List<Attachment> getAttachments() {
		return _attachments;
	}

	public String getAuthorName() {
		return _authorName;
	}

	public final String getContent() {
		return _content;
	}

	public String getLocation() {
		return _location;
	}

	public Date getPostedDateTime() {
		return _postedDateTime;
	}

	public void setAuthorName(final String name) {
		_authorName = name;
	}

	public void setContent(final String content) {
		_content = content;
	}

	public void setLocation(final String loc) {
		_location = loc;
	}

	public void setPostedDateTime(final Date dateTime) {
		_postedDateTime = dateTime;
	}
}
