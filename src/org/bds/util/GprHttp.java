package org.bds.util;

import org.bds.BdsLog;
import org.bds.Config;

import java.net.*;

/**
 * General purpose routines for Http
 */
public class GprHttp {

    public static final String ENV_PROXY_FTP = "ftp_proxy";
    public static final String ENV_PROXY_HTTTP = "http_proxy";
    public static final String ENV_PROXY_HTTTPS = "https_proxy";

    public static final int HTTP_OK = 200; // Connection OK
    public static final int HTTP_MOVED_PERMANENTLY = 301; // The requested resource moved permanently to a different URI
    public static final int HTTP_REDIR = 302; // The requested resource resides temporarily under a different URI
    public static final int HTTP_NOTFOUND = 404; // The requested resource resides temporarily under a different URI


    /**
     * Get proxy from environment variables
     */
    public static Proxy getProxyFromEnv() {
        Proxy proxy = getProxyFromEnv(ENV_PROXY_HTTTPS); // Try https first
        if (proxy != null) return proxy;
        return getProxyFromEnv(ENV_PROXY_HTTTP); // Not found? Use http
    }

    /**
     * Get proxy from environment variable 'envName'
     */
    public static Proxy getProxyFromEnv(String envName) {
        String proxyStr = System.getenv(envName);
        try {
            if (proxyStr != null) {
                URL proxyUrl = parseProxyUrl(proxyStr);
                if (proxyUrl == null) return null;
                InetSocketAddress proxyAddr = new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort());
                return new Proxy(Proxy.Type.HTTP, proxyAddr);
            }
        } catch (Exception e) {
            if (Config.get().isDebug())
                Gpr.debug("Error parsing proxy from environment '" + proxyStr + "', ignoring\n" + e.getMessage());
        }

        return null;
    }

    /**
     * Get proxy's URL from environment's variables
     */
    public static URL getProxyUrlFromEnv() {
        URL proxy = getProxyUrlFromEnv(ENV_PROXY_HTTTPS); // Try https first
        if (proxy != null) return proxy;
        return getProxyUrlFromEnv(ENV_PROXY_HTTTP); // Not found? Use http
    }

    /**
     * Get proxy's URL from environment variable 'envName'
     */
    public static URL getProxyUrlFromEnv(String envName) {
        String proxyStr = System.getenv(envName);
        return parseProxyUrl(proxyStr);
    }

    /**
     * Conect to URL and return a URL Connection
     *
     * @param url     : URL to conenct to
     * @param logger: Optional BdsLogger object to log messages and errors
     * @return URLConnection if successfull, null otherwise
     */
    public static URLConnection connect(URL url, BdsLog logger) {
        try {
            if (logger != null) logger.log("Connecting to " + url);

            // Open a connection using a proxy, if one found in the environment
            Proxy proxy = getProxyFromEnv();
            if (logger != null && proxy != null) logger.log("Using proxy " + proxy);
            URLConnection connection = (proxy != null ? url.openConnection(proxy) : url.openConnection());

            // Follow redirect? (only for http connections)
            if (connection instanceof HttpURLConnection) {
                while (true) {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int code = httpConnection.getResponseCode();

                    switch (code) {
                        case HTTP_OK:
                            // Status OK
                            return connection;

                        case HTTP_MOVED_PERMANENTLY:
                        case HTTP_REDIR:
                            String newUrl = connection.getHeaderField("Location");
                            if (logger != null) logger.log("Following redirect: " + newUrl);
                            url = new URL(newUrl);
                            connection = (proxy != null ? url.openConnection(proxy) : url.openConnection());
                            break;

                        case HTTP_NOTFOUND:
                            if (logger != null) logger.error("File '" + url + "' not found on server.");
                            return null;

                        default:
                            if (logger != null) logger.error("Server error " + code + " for URL '" + url + "'");
                            return null;
                    }
                }
            } else if (connection == null) {
                logger.debug("Could not connect to URL '" + url + "'");
            } else {
                logger.debug("Connection type different than expected '" + connection.getClass().getCanonicalName() + "', " + connection);
            }
        } catch (Exception e) {
            if (logger != null) logger.error("Error while connecting to " + url);
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Try to parse a Proxy URL (sometimes is not a well formed URL, just a "server:port")
     */
    public static URL parseProxyUrl(String proxyUrlStr) {
        if (proxyUrlStr == null) return null;

        var errMsg = "";
        try {
            return new URL(proxyUrlStr);
        } catch (MalformedURLException e) {
            // Ignore exception, probably missing protocol
            errMsg = e.getMessage(); // Store message to show it if next attempt fails again
        }

        // Usually protocol is missing, try adding protocol
        try {
            return new URL("https://" + proxyUrlStr);
        } catch (MalformedURLException e) {
            // Ignore exception, probably missing protocol
        }

        Gpr.debug("Error parsing proxy from environment '" + proxyUrlStr + "', ignoring. Error message: " + errMsg);
        return null;
    }
}
