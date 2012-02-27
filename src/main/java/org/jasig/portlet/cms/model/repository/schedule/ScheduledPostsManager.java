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
package org.jasig.portlet.cms.model.repository.schedule;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public final class ScheduledPostsManager {
	private static ScheduledPostsManager	instance	= null;

	public static ScheduledPostsManager getInstance() {
		if (instance == null)
			instance = new ScheduledPostsManager();
		return instance;
	}

	private Set<String>	repositoryRootsSet	= null;

	private ScheduledPostsManager() {
		repositoryRootsSet = new TreeSet<String>();
	}

	public void addRepositoryRoot(final String root) {
		repositoryRootsSet.add(root);
	}

	public boolean containsRoot(final String root) {
		return repositoryRootsSet.contains(root);
	}

	public Iterator<String> getRepositoryRoots() {
		return repositoryRootsSet.iterator();
	}

	public void removeRepositoryRoot(final String root) {
		repositoryRootsSet.remove(root);
	}

	public int size() {
		return repositoryRootsSet.size();
	}
}
