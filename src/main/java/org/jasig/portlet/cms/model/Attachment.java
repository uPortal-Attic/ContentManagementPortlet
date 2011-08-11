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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jcrom.JcrDataProvider;
import org.jcrom.JcrDataProviderImpl;
import org.jcrom.JcrFile;
import org.jcrom.annotations.JcrNode;
import org.jcrom.annotations.JcrProperty;

@JcrNode(nodeType = "nt:unstructured")
public class Attachment extends JcrFile {
	private static final long serialVersionUID = -6710317679666037037L;
	
	public static Attachment fromFile(final String fileName, final String contentType, final Date lastModified,
			final Locale lc, final byte[] contents) {
		
		final Attachment attachment = new Attachment();
		attachment.setMimeType(contentType);
		final Calendar c = Calendar.getInstance(lc);
		c.setTime(lastModified);
		attachment.setLastModified(c);
		
		attachment.setFileName(fileName);
		attachment.setContents(contents);
		return attachment;
	}
	
	@JcrProperty
	private String fileName;
	
	@JcrProperty
	private byte[] contents;
	
	@JcrProperty(name = "jcr:mimeType")
	private String contentType;
	
	@JcrProperty(name = "jcr:lastModified")
	private Calendar lastModified;
	
	@JcrProperty(name = "jcr:encoding")
	private String encoding;
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Attachment))
			return false;
		
		final Attachment rhs = (Attachment) obj;
		return new EqualsBuilder().append(getPath(), rhs.getPath()).isEquals();
	}
	
	public byte[] getContents() {
		return contents;
	}
	
	@Override
	public String getEncoding() {
		return encoding;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public Calendar getLastModified() {
		return lastModified;
	}
	
	@Override
	public String getMimeType() {
		return contentType;
	}
	
	@Override
	public String getName() {
		if (super.getName() == null)
			setName(getFileName());
		return super.getName();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getPath()).toHashCode();
	}
	
	public void setContents(final byte[] contents) {
		this.contents = contents;
		setDataProvider(new JcrDataProviderImpl(JcrDataProvider.TYPE.BYTES, contents));
	}
	
	@Override
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}
	
	
	public void setFileName(final String fileName) {
		this.fileName = fileName;
		setName(fileName);
	}
	
	@Override
	public void setLastModified(final Calendar lastModified) {
		this.lastModified = lastModified;
	}
	
	
	@Override
	public void setMimeType(final String mimeType) {
		contentType = mimeType;
	}
	
	@Override
	public String toString() {
		final ToStringBuilder bldr = new ToStringBuilder(this);
		bldr.append("name", getName());
		bldr.append("path", getPath());
		bldr.append("file", getFileName());
		bldr.append("encoding", getEncoding());
		bldr.append("mimeType", getMimeType());
		
		final SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		bldr.append("lastModified", format.format(getLastModified().getTime()));
		return bldr.toString();
	}
}
