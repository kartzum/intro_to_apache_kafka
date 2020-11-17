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

    public static void applyProxy() {
        String authUser = System.getenv("k_auth_user");
        String authPassword = System.getenv("k_auth_password");
        String httpProxyHost = System.getenv("k_http_proxy_host");
        String httpProxyPort = System.getenv("k_http_proxy_port");
        String httpsProxyHost = System.getenv("k_https_proxy_host");
        String httpsProxyPort = System.getenv("k_https_proxy_port");
        ConnectionUtils.proxy(
                authUser, authPassword, httpProxyHost, httpProxyPort, httpsProxyHost, httpsProxyPort);
    }
}
