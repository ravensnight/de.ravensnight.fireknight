# FireKnight Firewall Scripting
## In a Nutshell

FireKnight is a script converter which converts FireKnight scripts into iptables commands. FireKnight scripts have a syntax which is easy to understand and use. 

### Example:<br/>

***FireKnight Script:***

    # -----------------------------------------------------------------------------
    # Port definitions
    # -----------------------------------------------------------------------------
    # define interface localhost
    interface lo     127.0.0.1/32

    # define interface eth0, with ip address 192.168.0.1 and net mask 24
    interface eth0 192.168.0.2/24

    # -----------------------------------------------------------------------------
    # Services
    # -----------------------------------------------------------------------------

    # define a service, which requires any communication in and out
    service any {}

    # define ssh, client and server sides
    # both use tpc, port 22
    # source address for the server shall be restricted to the same net as the source interface belongs to
    # destination address for the server shall be restricted to the ip of the interface, where this service is attached to.
    service ssh {
        proto: tcp
        dport: 22

        server {
            saddr: $intf_in_net
            daddr: $intf_in_addr
        }

        client {
            # no restrictions except those from parent
        }
    }

    # -----------------------------------------------------------------------------
    # Rules / Assign services to interfaces
    # -----------------------------------------------------------------------------

    # local traffic
    @lo {
        consume any
        provide any
    }

    # ethernet interface
    @eth0 {

        # provide an ssh service on this machine/interface for external clients
        provide ssh
    
        # enable to use ssh from this machine to outside world via eth0
        consume ssh
    }


Now, when running FireKnight with certain feature options (ACCEPT_ESTABLISHED_RELATED, CLEANUP, LOG_SUSPECT, DROP_SUSPECT, SUDO) we get the following:

***iptables Script:***

    # Initial cleanup
    sudo iptables -t nat -F
    sudo iptables -t mangle -F
    sudo iptables -F
    sudo iptables -X

    # Setup suspect queues
    sudo iptables -N SUSPECT_INP
    sudo iptables -A SUSPECT_INP -m limit --limit 20/min -j LOG --log-prefix "[IPT:SUSPECT_INP]" --log-level 4
    sudo iptables -A SUSPECT_INP -j DROP
    sudo iptables -N SUSPECT_OUT
    sudo iptables -A SUSPECT_OUT -m limit --limit 20/min -j LOG --log-prefix "[IPT:SUSPECT_OUT]" --log-level 4
    sudo iptables -A SUSPECT_OUT -j DROP
    sudo iptables -N SUSPECT_FWD
    sudo iptables -A SUSPECT_FWD -m limit --limit 20/min -j LOG --log-prefix "[IPT:SUSPECT_FWD]" --log-level 4
    sudo iptables -A SUSPECT_FWD -j DROP

    # Accept ESTABLISHED/RELATED traffic
    sudo iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
    sudo iptables -A OUTPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
    sudo iptables -A FORWARD -m state --state ESTABLISHED,RELATED -j ACCEPT

    # Append rules to relevant queues
    sudo iptables -A OUTPUT -m state --state NEW -o lo -j ACCEPT
    sudo iptables -A INPUT -m state --state NEW -i lo -j ACCEPT
    sudo iptables -A INPUT -m state --state NEW -p tcp -i eth0 -s 192.168.0.0/24 -d 192.168.0.2 --dport 22 -j ACCEPT
    sudo iptables -A OUTPUT -m state --state NEW -p tcp -o eth0 --dport 22 -j ACCEPT
    # Route suspect packages
    sudo iptables -A INPUT -j SUSPECT_INP
    sudo iptables -A OUTPUT -j SUSPECT_OUT
    sudo iptables -A FORWARD -j SUSPECT_FWD
