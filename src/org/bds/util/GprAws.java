package org.bds.util;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URL;

/**
 * General purpose routines for AWS
 */
public class GprAws {

    /**
     * Create an ec2 client
     */
    public static Ec2Client ec2Client(String region) {
        Ec2ClientBuilder ec2b = Ec2Client.builder();

        // Do we have Proxy in the environment?
        URL proxyUrl = GprHttp.getProxyUrlFromEnv();
        if (proxyUrl != null) {
            // No proxy? Use default client
            ProxyConfiguration proxyConf = ProxyConfiguration.builder().useSystemPropertyValues(true).build();
            final SdkHttpClient httpClient = ApacheHttpClient.builder().proxyConfiguration(proxyConf).build();
            ec2b = ec2b.httpClient(httpClient);
        }

        // Region
        if (region != null && !region.isEmpty()) {
            Region r = Region.of(region);
            ec2b = ec2b.region(r);
        }

        return ec2b.build();
    }

    /**
     * Create s3 client
     */
    public static S3Client s3Client(String region) {
        S3ClientBuilder s3b = S3Client.builder();

        // Do we have Proxy in the environment?
        URL proxyUrl = GprHttp.getProxyUrlFromEnv();
        if (proxyUrl != null) {
            // No proxy? Use default client
            ProxyConfiguration proxyConf = ProxyConfiguration.builder().useSystemPropertyValues(true).build();
            final SdkHttpClient httpClient = ApacheHttpClient.builder().proxyConfiguration(proxyConf).build();
            s3b = s3b.httpClient(httpClient);
        }

        // Region
        if (region != null && !region.isEmpty()) {
            Region r = Region.of(region);
            s3b = s3b.region(r);
        }

        return s3b.build();
    }

}
