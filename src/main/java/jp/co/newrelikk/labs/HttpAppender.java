package jp.co.newrelikk.labs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.Encoder;

public class HttpAppender extends HttpAppenderAbstract {


	@Override
	public void append(ILoggingEvent event) {
		createIssue(event);
	}

	public void createIssue(ILoggingEvent event) {
		HttpURLConnection conn = null;

		try {
			URL urlObj = new URL(url);
			addInfo("URL: " + url);
			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod(method);
			transformHeaders(conn);
			boolean isOk = false;
			byte[] objEncoded = encoder.encode(event);
			if (method.equals("GET") || method.equals("DELETE")) {
				isOk = sendNoBodyRequest(conn);
			} else if (method.equals("POST") || method.equals("PUT")) {
				isOk = sendBodyRequest(objEncoded, conn);
			}

			if (!isOk) {
				addError("Not OK");
				return;
			}
		} catch (Exception e) {
			addError("Exception", e);
			return;
		} finally {
			try {
				if (conn != null) {
					conn.disconnect();
				}
			} catch (Exception e) {
				addError("Exception", e);
				return;
			}
		}
	}

	protected void transformHeaders(HttpURLConnection conn) {
		conn.setRequestProperty("Content-Type", contentType);
		if (headers == null || headers.isEmpty()) {
			return;
		}

		JSONObject jObj = new JSONObject(headers);
		for (String key : jObj.keySet()) {
			String value = (String) jObj.get(key);
			conn.setRequestProperty(key, value);
		}

	}

	protected boolean sendNoBodyRequest(HttpURLConnection conn) throws IOException {
		return showResponse(conn);
	}

	protected boolean sendBodyRequest(byte[] objEncoded, HttpURLConnection conn) throws IOException {
		conn.setDoOutput(true);
		if (body != null) {
			addInfo("Body: " + body);
			IOUtils.write(body, conn.getOutputStream(), Charset.defaultCharset());
		} else {
			IOUtils.write(objEncoded, conn.getOutputStream());
		}
		return showResponse(conn);
	}
}