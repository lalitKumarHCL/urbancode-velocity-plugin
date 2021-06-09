/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2017. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:  Use,
 * duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/
package com.ibm.devops.connect;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jenkins.model.Jenkins;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.devops.connect.SecuredActions.BuildJobsList;
import com.ibm.devops.connect.SecuredActions.BuildJobsList.BuildJobListParamObj;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import com.ibm.devops.connect.Endpoints.EndpointManager;


public class CloudSocketComponent {

    public static final Logger log = LoggerFactory.getLogger(CloudSocketComponent.class);

    final private IWorkListener workListener;
    final private String cloudUrl;

    private static Connection conn;

    private static boolean queueIsAvailable = false;
    private static boolean otherIntegrationExists = false;

    private static void setOtherIntegrationsExists(boolean exists) {
        otherIntegrationExists = exists;
    }

    public CloudSocketComponent(IWorkListener workListener, String cloudUrl) {
        this.workListener = workListener;
        this.cloudUrl = cloudUrl;
    }

    public void connectToCloudServices(int instanceNum) throws Exception {
        List<Entry> entries = Jenkins.getInstance().getDescriptorByType(DevOpsGlobalConfiguration.class).getEntries();
        if (entries.get(instanceNum).isConfigured()) {
            connectToAMQP(instanceNum);
            log.info("[UrbanCode Velocity "+(instanceNum+1)+ "] CloudSocketComponent#connectToCloudServices " + "Assembling list of Jenkins Jobs...");

            BuildJobsList buildJobList = new BuildJobsList();
            BuildJobListParamObj paramObj = buildJobList.new BuildJobListParamObj();
            buildJobList.runAsJenkinsUser(paramObj);
        }
    }

    public static boolean isAMQPConnected() {
        if (conn == null || queueIsAvailable == false) {
            return false;
        }
        return conn.isOpen();
    }

    public void connectToAMQP(int instanceNum) throws Exception {
        List<Entry> entries = Jenkins.getInstance().getDescriptorByType(DevOpsGlobalConfiguration.class).getEntries();
        if (!entries.get(instanceNum).isConfigured()) {
            return;
        }
        String syncId = entries.get(instanceNum).getSyncId();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(false);

        EndpointManager em = new EndpointManager();
        // Public Jenkins Client Credentials
        factory.setUsername("jenkins");
        factory.setPassword("jenkins");

        String host = em.getVelocityHostname(instanceNum);
        String rabbitHost = entries.get(instanceNum).getRabbitMQHost();
        if (rabbitHost != null && !rabbitHost.equals("")) {
            try {
                if (rabbitHost.endsWith("/")) {
                    rabbitHost = rabbitHost.substring(0, rabbitHost.length() - 1);
                }
                URL urlObj = new URL(rabbitHost);
                host = urlObj.getHost();
            } catch (MalformedURLException e) {
                log.warn("[UrbanCode Velocity "+(instanceNum+1)+ "] Provided Rabbit MQ Host is not a valid hostname. Using default : " + host, e);
            }
        }
        factory.setHost(host);

        int port = 5672;
        String rabbitPort = entries.get(instanceNum).getRabbitMQPort();

        if (rabbitPort != null && !rabbitPort.equals("")) {
            try {
                port = Integer.parseInt(rabbitPort);
            } catch (NumberFormatException nfe) {
                log.warn("[UrbanCode Velocity "+(instanceNum+1)+ "] Provided Rabbit MQ port is not an integer.  Using default 5672");
            }
        }
        factory.setPort(port);

        // Synchronized to protect manipulation of static variable
        synchronized (this) {

            if(this.conn != null && this.conn.isOpen()) {
                this.conn.abort();
            }
            
            conn = factory.newConnection();

            Channel channel = conn.createChannel();

            //log.info("[UrbanCode Velocity "+(instanceNum+1)+ "] Connecting to RabbitMQ");

            String EXCHANGE_NAME = "jenkins";
            String queueName = "jenkins.client." + syncId;

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                            AMQP.BasicProperties properties, byte[] body) throws IOException {

                    if (envelope.getRoutingKey().contains(".heartbeat")) {
                        String syncId = entries.get(instanceNum).getSyncId();
                        String syncToken = entries.get(instanceNum).getSyncToken();

                        String url = removeTrailingSlash(entries.get(instanceNum).getBaseUrl());
                        boolean connected = CloudPublisher.testConnection(syncId, syncToken, url);
                    } else {
                        String message = new String(body, "UTF-8");
                        System.out.println("[UrbanCode Velocity "+(instanceNum+1)+ "] [x] Received '" + message + "'");

                        CloudWorkListener2 cloudWorkListener = new CloudWorkListener2();
                        cloudWorkListener.call("startJob", message, instanceNum);
                    }
                }
            };

            if (checkQueueAvailability(channel, queueName, instanceNum)) {
                channel.basicConsume(queueName, true, consumer);
            }else{
                log.info("[UrbanCode Velocity "+(instanceNum+1)+ "] Queue is not yet available, will attempt to reconect shortly...");
                queueIsAvailable = false;
            }
        }
    }

    public static boolean checkQueueAvailability(Channel channel, String queueName, int instanceNum) throws IOException {
        try {
          channel.queueDeclarePassive(queueName);
          queueIsAvailable = true;
          return true;
        } catch (IOException e) {
            log.error("[UrbanCode Velocity "+(instanceNum+1)+ "] Checking Queue availability threw exception: ", e);
        }
        return false;
      }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String getCauseOfFailure() {
        if (otherIntegrationExists) {
            return "These credentials have been used by another Jenkins Instance.  Please generate another Sync Id and provide those credentials here.";
        }

        return null;
    }
}
