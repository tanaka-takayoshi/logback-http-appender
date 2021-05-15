package jp.co.newrelikk.labs;

import java.net.HttpURLConnection;
import java.net.URL;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.codec.binary.Base64;

/**
 * Provide basic http authentication.
 * 
 * @author Thiago Diniz da Silveira<thiagods.ti@gmail.com>
 *
 */
public class HttpAuthenticationAppender extends HttpAppenderBase<ILoggingEvent>
{

    private static final String SEPARATOR_BASIC_AUTHENTICATION = ":";

    protected Authentication authentication;
    protected String encondedUserPassword;

    @SuppressWarnings("restriction")
    @Override
    public void start()
    {
        super.start();
        if (authentication == null || authentication.isConfigured() == false)
        {
            addError("No authentication was configured. Use <authentication> to specify the <username> and the <password> for Basic Authentication.");
            return;
        }

        String userPassword = authentication.getUsername() + SEPARATOR_BASIC_AUTHENTICATION + authentication.getPassword();
        encondedUserPassword = Base64.encodeBase64String(userPassword.getBytes());

        addInfo("Using Basic Authentication");
    }

    @Override
    protected void configureHeadersImpl(HttpURLConnection conn) {
        conn.setRequestProperty("Authorization", "Basic " + encondedUserPassword);
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

}
