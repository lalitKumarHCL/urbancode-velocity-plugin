package com.ibm.devops.connect.Endpoints;

import jenkins.model.Jenkins;
import com.ibm.devops.connect.DevOpsGlobalConfiguration;

import java.net.URL;
import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.devops.connect.Entry;
import java.util.List;

public class EndpointsVelocity implements IEndpoints {
    private String logPrefix = "[EndpointsVelocity] EndpointsVelocity#";
    private static final Logger log = LoggerFactory.getLogger(EndpointsVelocity.class);

    private static final String SYNC_STORE_ENPOINT = "https://bogus/";
    private static final String CONNECT_ENPOINT = "https://bogus";
    private static final String REPORTING_SYNC_PATH = "/reporting-sync-api/";
    private static final String QUALITY_DATA_PATH = "/api/v1/metrics/upload";
    private static final String QUALITY_DATA_RAW_PATH = "/api/v1/metrics";
    private static final String RELEASE_EVENTS_API_PATH = "/release-events-api/";
    private static final String DOTS_PATH = "/api/v1/dots";
    private static final String PIPELINES_PATH = "/pipelines/";

    public String getReleaseEvensApiEndpoint(int i) {
        return getBaseUrl(i) + RELEASE_EVENTS_API_PATH;
    }

    public String getSyncApiEndpoint(int i) {
        return getBaseUrl(i) + REPORTING_SYNC_PATH;
    }

    public String getDotsEndpoint(int i) {
        return getBaseUrl(i) + DOTS_PATH;
    }

    public String getSyncApiEndpoint(String baseUrl) {
        baseUrl = removeTrailingSlash(baseUrl);
        return baseUrl + REPORTING_SYNC_PATH;
    }

    public String getPipelinesEndpoint(int i) {
        return getBaseUrl(i) + PIPELINES_PATH;
    }

    public String getQualityDataEndpoint(int i) {
        return getBaseUrl(i) + QUALITY_DATA_PATH;
    }

    public String getQualityDataRawEndpoint(int i) {
        return getBaseUrl(i) + QUALITY_DATA_RAW_PATH;
    }

    public String getSyncStoreEndpoint(int i) {
        return SYNC_STORE_ENPOINT;
    }

    public String getConnectEndpoint(int i) {
        return CONNECT_ENPOINT;
    }

    public String getVelocityHostname(int i) {
        try {
            String url = getBaseUrl(i);
            URL urlObj = new URL(url);
            return urlObj.getHost();
        } catch (MalformedURLException e) {
            log.error(logPrefix + "No valid URL was provided for Velocity: ", e);
        }
        return "";
    }

    private String getBaseUrl(int i) {
        List<Entry> entries = Jenkins.getInstance().getDescriptorByType(DevOpsGlobalConfiguration.class).getEntries();
        return removeTrailingSlash(entries.get(i).getBaseUrl());
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}