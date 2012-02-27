/**
 * Licensed to Jasig under one or more contributor license agreements. See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. Jasig licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jasig.portlet.cms.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.web.context.support.ServletContextResource;

/**
 * Spring 3 currently does not support Velocity Tools 2.0 and still uses some of
 * the deprecated classes that were available in 1.x. As a result, Velocity
 * toolbox configuration with Spring 3 is not possible without manually loading
 * the <code>toolbox.xml</code> file. <code>VelocityToolboxView</code> attempts
 * to configure the Velocity Toolbox View to use the new XML configuration in
 * Spring 3.
 */
public class VelocityToolboxView extends org.springframework.web.servlet.view.velocity.VelocityToolboxView {
	private final Log	logger	= LogFactory.getLog(getClass());

	@Override
	protected Context createVelocityContext(final Map<String, Object> model, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		final ViewToolContext velocityContext = new ViewToolContext(getVelocityEngine(), request, response,
				getServletContext());
		velocityContext.putAll(model);

		if (getToolboxConfigLocation() != null) {

			if (logger.isDebugEnabled())
				logger.debug("Configuring Velocity toolbox...");

			final XmlFactoryConfiguration cfg = new XmlFactoryConfiguration();
			cfg.read(new ServletContextResource(getServletContext(), getToolboxConfigLocation()).getURL());
			final ToolboxFactory factory = cfg.createFactory();

			velocityContext.addToolbox(factory.createToolbox(Scope.APPLICATION));
			velocityContext.addToolbox(factory.createToolbox(Scope.REQUEST));
			velocityContext.addToolbox(factory.createToolbox(Scope.SESSION));
		}

		return velocityContext;
	}

}
