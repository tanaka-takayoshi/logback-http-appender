package jp.co.newrelikk.labs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

public abstract class HttpAppenderBase<E> extends UnsynchronizedAppenderBase<E> {

	/**
	 * Defines default content type to send data.
	 */
	protected final static String DEFAULT_CONTENT_TYPE = "application/json";

	/**
	 * Defines default time in seconds to try to reconnect if connection is lost.
	 */
	protected final static int DEFAULT_RECONNECT_DELAY = 30;

	/**
	 * Defines default method to send data.
	 */
	protected final static String DEFAULT_METHOD = "POST";

	protected final String MSG_USING = "Using %s: %s";
	protected final String MSG_NOT_SET = "Assuming default value for %s: %s";

	protected Encoder<E> encoder;
	protected String url;
	protected String contentType;
	protected String headers;
	protected int reconnectDelay;
	protected String method;

	public void setEncoder(Encoder<E> encoder) {
		this.encoder = encoder;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public void setReconnectDelay(int reconnectDelay) {
		this.reconnectDelay = reconnectDelay;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public void start() {
		checkProperties();
		encoder.start();
		super.start();
	}

	protected void checkProperties() {
		if (encoder == null) {
			addError("No encoder was configured. Use <encoder> to specify the fully qualified class name of the encoder to use");
			return;
		}

		if (url == null || url.isEmpty()) {
			addError("No url was configured. Use <url> to specify the URL.");
			return;
		}

		if (contentType == null || contentType.isEmpty()) {
			contentType = DEFAULT_CONTENT_TYPE;
			addInfo(String.format(MSG_NOT_SET, "contentType", contentType));
		} else {
			addInfo(String.format(MSG_USING, "contentType", contentType));
		}

		if (reconnectDelay == 0) {
			reconnectDelay = DEFAULT_RECONNECT_DELAY;
			addInfo(String.format(MSG_NOT_SET, "reconnectDelay", reconnectDelay));
		} else {
			addInfo(String.format(MSG_USING, "reconnectDelay", reconnectDelay));
		}

		if (method == null || method.isEmpty()) {
			method = DEFAULT_METHOD;
			addInfo(String.format(MSG_NOT_SET, "method", method));
		} else {
			method = method.toUpperCase();
			addInfo(String.format(MSG_USING, "method", method));
		}
	}

	@Override
	public void append(E event) {
		HttpURLConnection conn = null;

		try {
			URL urlObj = new URL(url);

			conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod(method);
			configureHeaders(conn);
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

	private void configureHeaders(HttpURLConnection conn) {
		conn.setRequestProperty("Content-Type", contentType);
		if (headers == null || headers.isEmpty()) {
			return;
		}

		JSONObject jObj = new JSONObject(headers);
		for (String key : jObj.keySet()) {
			String value = (String) jObj.get(key);
			conn.setRequestProperty(key, value);
		}
		configureHeadersImpl(conn);
	}

	/**
	 * Configure additional request headers such as BASIC authentication.
	 * @param conn
	 */
	protected abstract void configureHeadersImpl(HttpURLConnection conn);

	protected boolean sendNoBodyRequest(HttpURLConnection conn) throws IOException {
		conn.connect();
		return showResponse(conn);
	}

	protected boolean sendBodyRequest(byte[] objEncoded, HttpURLConnection conn) throws IOException {
		conn.setDoOutput(true);
		conn.connect();
		IOUtils.write(objEncoded, conn.getOutputStream());
		return showResponse(conn);
	}
	
	protected void reconnect(E event) {
		if(reconnectDelay > 0) {
    		try {
    			addInfo(String.format("Trying to reconnect in %s seconds", reconnectDelay));
    			Thread.sleep(Duration.ofSeconds(reconnectDelay).toMillis());
    			append(event);
    		} catch (InterruptedException e1) {
    			addError("Error trying to reconnect: ", e1);
    			e1.printStackTrace();
    		}
	    }
	}

	protected boolean showResponse(HttpURLConnection conn) throws IOException {
		int responseCode = conn.getResponseCode();
		String response = IOUtils.toString(conn.getInputStream(), Charset.defaultCharset());
		addInfo(String.format("Response result: %s", response));

		if (responseCode >= 300) {
			addError(String.format("Error to send logs: %s", conn));
			return false;
		}

		return true;
	}
//
//	protected HttpURLConnection openConnection() {
//		HttpURLConnection conn = null;
//		try {
//			URL urlObj = new URL(protocol, url, port, path);
//			addInfo("URL: " + urlObj);
//			conn = (HttpURLConnection) urlObj.openConnection();
//			conn.setRequestMethod("POST");
//			return conn;
//		} catch (Exception e) {
//			addError("Error to open connection Exception: ", e);
//			return null;
//		} finally {
//			try {
//				if (conn != null) {
//					conn.disconnect();
//				}
//			} catch (Exception e) {
//				addError("Error to open connection Exception: ", e);
//				return null;
//			}
//		}
//	}

}