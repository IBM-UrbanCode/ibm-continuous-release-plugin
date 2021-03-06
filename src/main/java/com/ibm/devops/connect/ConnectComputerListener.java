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
import hudson.model.Computer;
import jenkins.model.Jenkins.MasterComputer;
import hudson.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.devops.connect.CloudItemListener;

import com.ibm.devops.connect.Endpoints.EndpointManager;

@Extension
public class ConnectComputerListener extends ComputerListener {
	public static final Logger log = LoggerFactory.getLogger(ConnectComputerListener.class);
    private String logPrefix= "[IBM Cloud DevOps] ConnectComputerListener#";

    private static CloudSocketComponent cloudSocketInstance;

    private static void setCloudSocketComponent( CloudSocketComponent comp ) {
        cloudSocketInstance = comp;
    }

    @Override
    public void onOnline(Computer c) {
        if ( c instanceof jenkins.model.Jenkins.MasterComputer ) {
            logPrefix= logPrefix + "onOnline ";
            String url = getConnectUrl();

            CloudWorkListener listener = new CloudWorkListener();

            if(cloudSocketInstance != null && cloudSocketInstance.connected()) {
                cloudSocketInstance.disconnect();
            }

            ConnectComputerListener.setCloudSocketComponent(new CloudSocketComponent(listener, url));

            try {
                log.info(logPrefix + "Connecting to Cloud Services...");
                getCloudSocketInstance().connectToCloudServices();
            } catch (Exception e) {
                log.error(logPrefix + "Exception caught while connecting to Cloud Services: " + e);
            }

        }
    }

    private String getConnectUrl() {
        EndpointManager em = new EndpointManager();
        return em.getConnectEndpoint();
    }

    public CloudSocketComponent getCloudSocketInstance() {
        return ConnectComputerListener.cloudSocketInstance;
    }
}