package de.ravensnight.fireknight.common;

public enum NamedPort {
    ftp(21),
    ssh(22),    
    dns(53), 
    dhcpc(68), 
    dhcps(67), 
    http(80), 
    https(443), 
    ntp(123), 
    nfs(2049), 
    nfsrpc(111);

    private final int port;

    NamedPort(int num) {
        this.port = num;
    }

    public int getPort() {
        return this.port;
    }
}
