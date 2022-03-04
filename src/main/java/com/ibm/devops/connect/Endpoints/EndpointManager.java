package com.ibm.devops.connect.Endpoints;

import com.ibm.devops.connect.Entry;

public class EndpointManager {

    // TODO: Make configurable at build time or otherwise
    // private static String profile = "Velocity";
    // private static String profile = "YS1";

    private IEndpoints endpointProvider;

    public EndpointManager() {
        endpointProvider = new EndpointsVelocity();
    }

    public String getSyncApiEndpoint(Entry entry) {
        return endpointProvider.getSyncApiEndpoint(entry);
    }

    public String getGraphqlApiEndpoint(Entry entry) {
        return endpointProvider.getGraphqlApiEndpoint(entry);
    }

    public String getPipelinesEndpoint(Entry entry) {
        return endpointProvider.getPipelinesEndpoint(entry);
    }

    public String getQualityDataEndpoint(Entry entry) {
        return endpointProvider.getQualityDataEndpoint(entry);
    }

    public String getQualityDataRawEndpoint(Entry entry) {
        return endpointProvider.getQualityDataRawEndpoint(entry);
    }

    public String getSyncApiEndpoint(String baseUrl) {
        return endpointProvider.getSyncApiEndpoint(baseUrl);
    }

    public String getReleaseEvensApiEndpoint(Entry entry) {
        return endpointProvider.getReleaseEvensApiEndpoint(entry);
    }

    public String getDotsEndpoint(Entry entry) {
        return endpointProvider.getDotsEndpoint(entry);
    }

    public String getSyncStoreEndpoint(Entry entry) {
        return endpointProvider.getSyncStoreEndpoint(entry);
    }

    public String getConnectEndpoint(Entry entry) {
        return endpointProvider.getConnectEndpoint(entry);
    }

    public String getVelocityHostname(Entry entry) {
        return endpointProvider.getVelocityHostname(entry);
    }
}