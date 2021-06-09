package com.ibm.devops.connect.Endpoints;

public class EndpointManager {

    // TODO: Make configurable at build time or otherwise
    private static String profile = "Velocity";
    //private static String profile = "YS1";

    private IEndpoints endpointProvider;

    public EndpointManager() {
        endpointProvider = new EndpointsVelocity();
    }

    public String getSyncApiEndpoint(int i) {
        return endpointProvider.getSyncApiEndpoint(i);
    }

    public String getPipelinesEndpoint(int i) {
        return endpointProvider.getPipelinesEndpoint(i);
    }

    public String getQualityDataEndpoint(int i) {
        return endpointProvider.getQualityDataEndpoint(i);
    }

    public String getQualityDataRawEndpoint(int i) {
        return endpointProvider.getQualityDataRawEndpoint(i);
    }

    public String getSyncApiEndpoint(String baseUrl) {
        return endpointProvider.getSyncApiEndpoint(baseUrl);
    }

    public String getReleaseEvensApiEndpoint(int i) {
        return endpointProvider.getReleaseEvensApiEndpoint(i);
    }

    public String getDotsEndpoint(int i) {
        return endpointProvider.getDotsEndpoint(i);
    }

    public String getSyncStoreEndpoint(int i) {
        return endpointProvider.getSyncStoreEndpoint(i);
    }

    public String getConnectEndpoint(int i) {
        return endpointProvider.getConnectEndpoint(i);
    }

    public String getVelocityHostname(int i) {
        return endpointProvider.getVelocityHostname(i);
    }
}
