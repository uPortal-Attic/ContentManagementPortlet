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

package org.jasig.portlet.cms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jcrom.annotations.JcrChildNode;
import org.jcrom.annotations.JcrName;
import org.jcrom.annotations.JcrNode;
import org.jcrom.annotations.JcrPath;
import org.jcrom.annotations.JcrProperty;
import org.jcrom.annotations.JcrUUID;

@JcrNode(mixinTypes = { "mix:versionable" })
public class Post implements Serializable {

	private static final long	serialVersionUID	= 6392459877566744761L;

	@JcrChildNode
	private List<Attachment>	attachments			= null;

	@JcrProperty
	private String				author				= null;

	@JcrProperty
	private String				content				= null;

	@JcrProperty
	private String				language			= null;

	@JcrProperty
	private String				lastModifiedDate	= null;

	@JcrName
	private String				name;

	@JcrPath
	private String				path				= null;

	@JcrProperty
	private long				rate				= 0;

	@JcrProperty
	private long				rateCount			= 0;

	@JcrProperty
	private String				scheduledDate		= null;

	@JcrUUID
	private String				uuid				= null;

	public Post() {
		setAttachments(new ArrayList<Attachment>());
		setLanguage(Locale.getDefault().getLanguage());
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

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public Locale getLocale() {
		return new Locale(getLanguage());
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public long getRate() {
		return rate;
	}

	public long getRateCount() {
		return rateCount;
	}

	public String getScheduledDate() {
		return scheduledDate;
	}

	public String getUuid() {
		return uuid;
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

	public void setLanguage(final String lang) {
		language = lang;
	}

	public void setLastModifiedDate(final String date) {
		lastModifiedDate = date;
	}

	public void setPath(final String loc) {
		path = loc;
		name = loc;
	}

	public void setRate(final long rate) {
		this.rate = rate;
	}

	public void setRateCount(final long rateCount) {
		this.rateCount = rateCount;
	}

	public void setScheduledDate(final String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("author", getAuthor()).append("language", getLanguage())
				.append("lastModifiedDate", getLastModifiedDate()).append("name", getName()).append("rate", getRate())
				.append("scheduledDate", getScheduledDate()).append("uuid", getUuid()).append("path", getPath())
				.append("numberOfAttachments", getAttachments().size()).toString();
	}

	private String getLanguage() {
		return language;
	}

}
