package com.ibm.devops.connect.Endpoints;

import com.ibm.devops.connect.Entry;

public interface IEndpoints {

    public String getSyncApiEndpoint(Entry entry);

    public String getGraphqlApiEndpoint(Entry entry);

    public String getPipelinesEndpoint(Entry entry);

    public String getSyncApiEndpoint(String baseUrl);

    public String getSyncStoreEndpoint(Entry entry);

    public String getConnectEndpoint(Entry entry);

    public String getQualityDataEndpoint(Entry entry);

    public String getQualityDataRawEndpoint(Entry entry);

    public String getVelocityHostname(Entry entry);

    public String getReleaseEvensApiEndpoint(Entry entry);

    public String getDotsEndpoint(Entry entry);
}