package com.ibm.devops.connect.Endpoints;

public interface IEndpoints {

    public String getSyncApiEndpoint(int i);

    public String getPipelinesEndpoint(int i);

    public String getSyncApiEndpoint(String baseUrl);

    public String getSyncStoreEndpoint(int i);

    public String getConnectEndpoint(int i);

    public String getQualityDataEndpoint(int i);

    public String getQualityDataRawEndpoint(int i);

    public String getVelocityHostname(int i);

    public String getReleaseEvensApiEndpoint(int i);

    public String getDotsEndpoint(int i);
}