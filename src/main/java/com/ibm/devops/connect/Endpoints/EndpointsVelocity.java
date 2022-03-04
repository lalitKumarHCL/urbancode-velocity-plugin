package com.ibm.devops.connect.Endpoints;

import jenkins.model.Jenkins;

import com.ibm.devops.connect.CloudPublisher;
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
    private static final String GRAPHQL_PATH = "/release-events-api/graphql/";

    public String getReleaseEvensApiEndpoint(Entry entry) {
        return getBaseUrl(entry) + RELEASE_EVENTS_API_PATH;
    }

    public String getGraphqlApiEndpoint(Entry entry) {
        return getBaseUrl(entry) + GRAPHQL_PATH;
    }

    public String getSyncApiEndpoint(Entry entry) {
        return getBaseUrl(entry) + REPORTING_SYNC_PATH;
    }

    public String getDotsEndpoint(Entry entry) {
        return getBaseUrl(entry) + DOTS_PATH;
    }

    public String getSyncApiEndpoint(String baseUrl) {
        baseUrl = CloudPublisher.removeTrailingSlash(baseUrl);
        return baseUrl + REPORTING_SYNC_PATH;
    }

    public String getPipelinesEndpoint(Entry entry) {
        return getBaseUrl(entry) + PIPELINES_PATH;
    }

    public String getQualityDataEndpoint(Entry entry) {
        return getBaseUrl(entry) + QUALITY_DATA_PATH;
    }

    public String getQualityDataRawEndpoint(Entry entry) {
        return getBaseUrl(entry) + QUALITY_DATA_RAW_PATH;
    }

    public String getSyncStoreEndpoint(Entry entry) {
        return SYNC_STORE_ENPOINT;
    }

    public String getConnectEndpoint(Entry entry) {
        return CONNECT_ENPOINT;
    }

    public String getVelocityHostname(Entry entry) {
        try {
            String url = getBaseUrl(entry);
            URL urlObj = new URL(url);
            return urlObj.getHost();
        } catch (MalformedURLException e) {
            log.error(logPrefix + "No valid URL was provided for Velocity: ", e);
        }
        return "";
    }

    private String getBaseUrl(Entry entry) {
        return CloudPublisher.removeTrailingSlash(entry.getBaseUrl());
    }

}