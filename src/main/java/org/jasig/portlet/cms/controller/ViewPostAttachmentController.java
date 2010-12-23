package org.jasig.portlet.cms.controller;

import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.model.Attachment;
import org.jasig.portlet.cms.model.Post;
import org.jasig.portlet.cms.model.repository.RepositoryDao;
import org.springframework.web.portlet.bind.PortletRequestUtils;
import org.springframework.web.portlet.mvc.AbstractController;

public class ViewPostAttachmentController extends AbstractController {
	private final Log logger = LogFactory.getLog(getClass());

	private RepositoryDao repositoryDao = null;


	public void setRepositoryDao(final RepositoryDao repositoryDao) {
		this.repositoryDao = repositoryDao;
	}

	private RepositoryDao getRepositoryDao() {
		return repositoryDao;
	}

	@Override
	protected void handleActionRequestInternal(final ActionRequest request, final ActionResponse response) throws Exception {

		final String attachmentPath = PortletRequestUtils.getRequiredStringParameter(request, "attachmentPath");
		final String postPath = PortletRequestUtils.getRequiredStringParameter(request, "postPath");

		if (logger.isDebugEnabled())
			logger.debug("Attachment path to remove: " + attachmentPath);

		final Post post = getRepositoryDao().getPost(postPath);
		if (post != null) {

			if (logger.isDebugEnabled())
				logger.debug("Retrieved repository post " + post);

			final Iterator<Attachment> it = post.getAttachments().iterator();

			Attachment attachment = null;
			while (it.hasNext()) {
				attachment = it.next();
				if (attachment.getPath().equals(attachmentPath)) {
					if (logger.isDebugEnabled())
						logger.debug("Found post attachment: " + attachment);

					request.getPortletSession()
					.setAttribute("attachment", attachment, PortletSession.APPLICATION_SCOPE);

					final String contextPath = request.getContextPath();

					final String url = new StringBuilder().append(contextPath).append("/")
					.append("downloadPostAttachment").toString();

					if (logger.isDebugEnabled())
						logger.debug("Url is: " + url);

					response.sendRedirect(url);
				}
			}

		}


	}


}
