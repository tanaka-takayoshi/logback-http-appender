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

public class HttpAppender extends HttpAppenderBase<ILoggingEvent> {

    @Override
    protected void configureHeadersImpl(HttpURLConnection conn) {
        //do nothing
    }
}