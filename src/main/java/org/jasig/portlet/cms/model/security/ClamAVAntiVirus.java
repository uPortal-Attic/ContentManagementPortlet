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
package org.jasig.portlet.cms.model.security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ClamAVAntiVirus implements AntiVirusService {
	private final Log logger = LogFactory.getLog(getClass());
	private String ip;
	private int port;

	public ClamAVAntiVirus() {
		setIp("127.0.0.1");
		setPort(3310);
	}

	@Override
	public void scan(final File file) throws AntiVirusException {
		final String filePath = file.getAbsolutePath();
		if (!file.exists())
			throw new AntiVirusException(file, "antivirus.scan.file.not.found");

		if (file.length() <= 0)
			throw new AntiVirusException(file, "antivirus.scan.file.empty");

		final Socket socket = connect();
		if (socket == null)
			throw new AntiVirusException(file, "antivirus.scan.engine.offline");

		try {
			final String results = sendSocket(socket, "SCAN " + filePath);
			if (results.indexOf("OK") == -1) {
				final String virus = results.substring(results.indexOf(':') + 1);
				if (file.exists())
					file.delete();
				throw new AntiVirusException(file, virus);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			throw new AntiVirusException(file, "");
		} finally {
			closeSocket(socket);
		}
	}

	public void setIp(final String ip) {
		this.ip = ip;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	private void closeSocket(final Socket socket) {
		try {
			if (socket != null)
				socket.close();
		} catch (final Exception e) {

		}
	}

	private Socket connect() {
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
		} catch (final Exception e) {
			socket = null;
			logger.error(e.getMessage(), e);
		}
		return socket;
	}

	private String sendSocket(final Socket socket, final String command) throws Exception {
		String answer = null;
		BufferedReader reader = null;
		PrintWriter writer = null;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
			        true);

			writer.println(command);
			writer.flush();

			answer = reader.readLine();
			if (answer != null)
				answer = answer.trim();

		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (final IOException e) {
				logger.error(e.getMessage(), e);
			}

			if (writer != null)
				writer.close();
		}
		return answer;
	}
}
