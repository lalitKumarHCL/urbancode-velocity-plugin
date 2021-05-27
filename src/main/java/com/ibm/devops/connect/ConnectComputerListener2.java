/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2017. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:  Use,
 * duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/
package com.ibm.devops.connect;

import hudson.slaves.ComputerListener;
import jenkins.model.Jenkins;
import hudson.model.Computer;
import hudson.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.devops.connect.Endpoints.EndpointManager2;

import com.ibm.devops.connect.ReconnectExecutor2;

@Extension
public class ConnectComputerListener2 extends ComputerListener {
	public static final Logger log = LoggerFactory.getLogger(ConnectComputerListener2.class);
    private String logPrefix= "[UrbanCode Velocity2] ConnectComputerListener#";

    private static CloudSocketComponent2 cloudSocketInstance;
    private static ReconnectExecutor2 reconnectExecutor;

    private static void setCloudSocketComponent( CloudSocketComponent2 comp ) {
        cloudSocketInstance = comp;
    }

    @Override
    public void onOnline(Computer c) {
        if ( c instanceof jenkins.model.Jenkins.MasterComputer && Jenkins.getInstance().getDescriptorByType(DevOpsGlobalConfiguration.class).isConfigured2()) {
            logPrefix= logPrefix + "onOnline ";
            String url = getConnectUrl();

            CloudWorkListener listener = new CloudWorkListener();

            ConnectComputerListener2.setCloudSocketComponent(new CloudSocketComponent2(listener, url));

            try {
                log.info(logPrefix + "Connecting to Cloud Services for 2nd Instance...");
                getCloudSocketInstance().connectToCloudServices();
            } catch (Exception e) {
                log.error(logPrefix + "Exception caught while connecting to Cloud Services for 2nd Instance: " + e);
                e.printStackTrace();
            }

            // Synchronized to protect lazy initalization of static variable
            synchronized(this) {
                if(reconnectExecutor == null) {
                    reconnectExecutor = new ReconnectExecutor2(cloudSocketInstance);
                    reconnectExecutor.startReconnectExecutor();
                }
            }
        }
    }

    private String getConnectUrl() {
        EndpointManager2 em = new EndpointManager2();
        return em.getConnectEndpoint();
    }

    public CloudSocketComponent2 getCloudSocketInstance() {
        return ConnectComputerListener2.cloudSocketInstance;
    }
}