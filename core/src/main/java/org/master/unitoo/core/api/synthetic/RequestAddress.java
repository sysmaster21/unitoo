/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

/**
 *
 * @author Andrey
 */
public class RequestAddress {

    private final String remoteHostName;
    private final String remoteHostIP;
    private final int remotePort;
    private final String localHostName;
    private final String localHostIP;
    private final int localPort;

    public RequestAddress(String remoteHostName, String remoteHostIP, int remotePort, String localHostName, String localHostIP, int localPort) {
        this.remoteHostName = remoteHostName;
        this.remoteHostIP = remoteHostIP;
        this.remotePort = remotePort;
        this.localHostName = localHostName;
        this.localHostIP = localHostIP;
        this.localPort = localPort;
    }

    public String getRemoteHostName() {
        return remoteHostName;
    }

    public String getRemoteHostIP() {
        return remoteHostIP;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getLocalHostName() {
        return localHostName;
    }

    public String getLocalHostIP() {
        return localHostIP;
    }

    public int getLocalPort() {
        return localPort;
    }

}
