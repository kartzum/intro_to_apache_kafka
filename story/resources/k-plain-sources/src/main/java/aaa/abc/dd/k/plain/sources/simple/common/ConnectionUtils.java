package aaa.abc.dd.k.plain.sources.simple.common;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ConnectionUtils {
    public static void proxy(
            String authUser,
            String authPassword,
            String httpProxyHost,
            String httpProxyPort,
            String httpsProxyHost,
            String httpsProxyPort
    ) {
        System.setProperty("http.proxyHost", httpProxyHost);
        System.setProperty("http.proxyPort", httpProxyPort);
        System.setProperty("https.proxyHost", httpsProxyHost);
        System.setProperty("https.proxyPort", httpsProxyPort);
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

        Authenticator.setDefault(
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(authUser, authPassword.toCharArray());
                    }
                }
        );

        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        Ssl2.disableSSLCertificateChecking();
    }
}
