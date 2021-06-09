package com.ibm.devops.connect;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import hudson.util.FormValidation;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.util.List;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;


public final class Entry implements Describable<Entry> {

    /**
     * Destination bucket for the copy. Can contain macros.
     */
    private String syncId;
    private String syncToken;
    private String baseUrl;
    private String rabbitMQPort;
    private String rabbitMQHost;
    private String apiToken;
    

    @DataBoundConstructor
    public Entry(String syncId, String syncToken, String baseUrl, String rabbitMQPort, String rabbitMQHost, String apiToken) {
        this.syncId = syncId;
        this.syncToken = syncToken;
        this.baseUrl = baseUrl;
        this.rabbitMQPort = rabbitMQPort;
        this.rabbitMQHost = rabbitMQHost;
        this.apiToken = apiToken;
    }

    public String getSyncId() {
        return syncId;
    }
    public String getSyncToken() {
        return syncToken;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public String getRabbitMQPort() {
        return rabbitMQPort;
    }
    public String getRabbitMQHost() {
        return rabbitMQHost;
    }
    public String getApiToken() {
        return apiToken;
    }

    public boolean isConfigured() {
        return StringUtils.isNotEmpty(this.syncId) &&
               StringUtils.isNotEmpty(this.syncToken) &&
               StringUtils.isNotEmpty(this.baseUrl) &&
               StringUtils.isNotEmpty(this.apiToken);
    }


    @Override
    public Descriptor<Entry> getDescriptor() {
        return DESCRIPOR;
    }

    @Extension
    public static final DescriptorImpl DESCRIPOR = new DescriptorImpl();

    public static class DescriptorImpl extends  Descriptor<Entry> {
        @Override
        public String getDisplayName() {
            return "Connect to UrbanCode Velocity Instance";
        }


        @SuppressWarnings("unused")
        @RequirePOST
        public FormValidation doTestConnections(@QueryParameter("syncId") String syncId,
        @QueryParameter("syncToken") String syncToken,
        @QueryParameter("baseUrl") String baseUrl){
        //Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
            try {
                boolean connected = CloudPublisher.testConnection(syncId, syncToken, baseUrl);
                if (connected) {
                    boolean amqpConnected = CloudSocketComponent.isAMQPConnected();

                    String rabbitMessage = "Not connected to RabbitMQ. Unable to run Jenkins jobs from UCV.";
                    if(amqpConnected) {
                        rabbitMessage = "Connected to RabbitMQ successfully. Ready to run Jenkins jobs from UCV.";
                    }

                    return FormValidation.ok("Successful connection to Velocity Services.\n" + rabbitMessage);
                } else {
                    return FormValidation.error("Could not connect to Velocity.  Please check your URL and credentials provided.");
                }
            } catch (Exception e) {
                return FormValidation.error("Could not connect to Velocity : " + e.getMessage());
            }
        }
        

    }


}
