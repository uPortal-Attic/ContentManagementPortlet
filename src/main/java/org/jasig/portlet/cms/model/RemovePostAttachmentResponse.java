package org.jasig.portlet.cms.model;

public class RemovePostAttachmentResponse {
	private Attachment removedAttachment = null;
	private boolean removeSuccessful = false;

	public Attachment getRemovedAttachment() {
		return removedAttachment;
	}

	public boolean isRemoveSuccessful() {
		return removeSuccessful;
	}

	public void setRemovedAttachment(final Attachment attachmentToRemove) {
		removedAttachment = attachmentToRemove;
	}

	public void setRemoveSuccessful(final boolean removeSuccessful) {
		this.removeSuccessful = removeSuccessful;
	}

}