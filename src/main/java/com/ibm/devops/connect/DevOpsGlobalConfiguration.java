/*
 <notice>

 Copyright 2016, 2017 IBM Corporation

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 </notice>
 */

package com.ibm.devops.connect;

import java.util.List;

import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.util.ListBoxModel;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.security.ACL;
import jenkins.model.Jenkins;

/**
 * Created by lix on 7/20/17.
 */
@Extension(ordinal = 100)
public class DevOpsGlobalConfiguration extends GlobalConfiguration {

    @CopyOnWrite
    private volatile String syncId;
    private volatile String syncToken;
    private volatile String baseUrl;
    private volatile String syncId2;
    private volatile String syncToken2;
    private volatile String baseUrl2;
    private String credentialsId;
    private String rabbitMQPort;
    private String rabbitMQHost;
    private String apiToken;
    private String rabbitMQPort2;
    private String rabbitMQHost2;
    private String apiToken2;

    public DevOpsGlobalConfiguration() {
        load();
    }

    public String getSyncId() {
    	return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
        save();
    }

    public String getSyncToken() {
    	return syncToken;
    }

    public void setSyncToken(String syncToken) {
        this.syncToken = syncToken;
        save();
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
        save();
    }

    public String getBaseUrl() {
    	return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        save();
    }

    public String getSyncId2() {
    	return syncId2;
    }

    public void setSyncId2(String syncId2) {
        this.syncId2 = syncId2;
        save();
    }

    public String getSyncToken2() {
    	return syncToken2;
    }

    public void setSyncToken2(String syncToken2) {
        this.syncToken2 = syncToken2;
        save();
    }

    public String getApiToken2() {
        return apiToken2;
    }

    public void setApiToken2(String apiToken2) {
        this.apiToken2 = apiToken2;
        save();
    }

    public String getBaseUrl2() {
    	return baseUrl2;
    }

    public void setBaseUrl2(String baseUrl2) {
        this.baseUrl2 = baseUrl2;
        save();
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
        save();
    }

    public String getRabbitMQPort() {
        return rabbitMQPort;
    }

    public String getRabbitMQHost() {
        return rabbitMQHost;
    }

    public void setRabbitMQPort(String rabbitMQPort) {
        this.rabbitMQPort = rabbitMQPort;
        save();
    }

    public void setRabbitMQHost(String rabbitMQHost) {
        this.rabbitMQHost = rabbitMQHost;
        save();
    }

    public String getRabbitMQPort2() {
        return rabbitMQPort2;
    }

    public String getRabbitMQHost2() {
        return rabbitMQHost2;
    }

    public void setRabbitMQPort2(String rabbitMQPort2) {
        this.rabbitMQPort2 = rabbitMQPort2;
        save();
    }

    public void setRabbitMQHost2(String rabbitMQHost2) {
        this.rabbitMQHost2 = rabbitMQHost2;
        save();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        // To persist global configuration information,
        // set that to properties and call save().
        syncId = formData.getString("syncId");
        syncToken = formData.getString("syncToken");
        baseUrl = formData.getString("baseUrl");
        credentialsId = formData.getString("credentialsId");
        rabbitMQPort = formData.getString("rabbitMQPort");
        rabbitMQHost = formData.getString("rabbitMQHost");
        apiToken = formData.getString("apiToken");
        if (Jenkins.getInstance().getDescriptorByType(DevOpsGlobalConfiguration.class).isConfigured2()) {
            syncId2 = formData.getString("syncId2");
            syncToken2 = formData.getString("syncToken2");
            baseUrl2 = formData.getString("baseUrl2");
            rabbitMQPort2 = formData.getString("rabbitMQPort2");
            rabbitMQHost2 = formData.getString("rabbitMQHost2");
            apiToken2 = formData.getString("apiToken2");
        }
        save();

        reconnectCloudSocket();
        if (Jenkins.getInstance().getDescriptorByType(DevOpsGlobalConfiguration.class).isConfigured2()) {
            reconnectCloudSocket2();
        }
        return super.configure(req,formData);
    }

    // for the future multi-region use
    public ListBoxModel doFillRegionItems() {
        ListBoxModel items = new ListBoxModel();
        return items;
    }

    public FormValidation doTestConnection(@QueryParameter("syncId") final String syncId,
        @QueryParameter("syncToken") final String syncToken,
        @QueryParameter("baseUrl") final String baseUrl)
    throws FormException {
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

    public FormValidation doTestConnection2(@QueryParameter("syncId2") final String syncId,
        @QueryParameter("syncToken2") final String syncToken,
        @QueryParameter("baseUrl2") final String baseUrl)
    throws FormException {
        try {
            
            boolean connected = CloudPublisher.testConnection2(syncId, syncToken, baseUrl);
            if (connected) {
                boolean amqpConnected = CloudSocketComponent2.isAMQPConnected();
                String rabbitMessage = "Not connected to RabbitMQ. Unable to run Jenkins jobs from UCV 2nd Instance.";
                if(amqpConnected) {
                    rabbitMessage = "Connected to RabbitMQ successfully. Ready to run Jenkins jobs from UCV 2nd Instance.";
                }

                return FormValidation.ok("Successful connection to Velocity 2nd Instance Services.\n" + rabbitMessage);
            } else {
                return FormValidation.error("Could not connect to Velocity 2nd Instance.  Please check your URL and credentials provided.");
            }
        } catch (Exception e) {
            return FormValidation.error("Could not connect to Velocity 2nd Instance : " + e.getMessage());
        }
    }

    /**
    * This method is called to populate the credentials list on the Jenkins config page.
    */
    public ListBoxModel doFillCredentialsIdItems(@QueryParameter("target") final String target) {
        StandardListBoxModel result = new StandardListBoxModel();
        result.includeEmptyValue();
        result.withMatching(CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                CredentialsProvider.lookupCredentials(
                        StandardUsernameCredentials.class,
                        Jenkins.getInstance(),
                        ACL.SYSTEM,
                        URIRequirementBuilder.fromUri(target).build()
                )
        );
        return result;
    }

    public StandardUsernamePasswordCredentials getCredentialsObj() {
        List<StandardUsernamePasswordCredentials> standardCredentials = CredentialsProvider.lookupCredentials(
                    StandardUsernamePasswordCredentials.class,
                    Jenkins.getInstance(),
                    ACL.SYSTEM);

        StandardUsernamePasswordCredentials credentials =
                CredentialsMatchers.firstOrNull(standardCredentials, CredentialsMatchers.withId(this.credentialsId));

        return credentials;
    }

    private void reconnectCloudSocket() {
        ConnectComputerListener connectComputerListener = new ConnectComputerListener();

        connectComputerListener.onOnline(Jenkins.getInstance().toComputer());
    }
    private void reconnectCloudSocket2() {
        ConnectComputerListener2 connectComputerListener2 = new ConnectComputerListener2();

        connectComputerListener2.onOnline(Jenkins.getInstance().toComputer());
    }

    public boolean isConfigured() {
        return StringUtils.isNotEmpty(this.syncId) &&
               StringUtils.isNotEmpty(this.syncToken) &&
               StringUtils.isNotEmpty(this.baseUrl) &&
               StringUtils.isNotEmpty(this.credentialsId) &&
               StringUtils.isNotEmpty(this.apiToken);
    }
    public boolean isConfigured2() {
        return StringUtils.isNotEmpty(this.syncId2) &&
               StringUtils.isNotEmpty(this.syncToken2) &&
               StringUtils.isNotEmpty(this.baseUrl2) &&
               StringUtils.isNotEmpty(this.credentialsId) &&
               StringUtils.isNotEmpty(this.apiToken2);
    }
}
