package de.ravensnight.fireknight.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class ShellScriptWriter {

    private final BufferedWriter out;
    private boolean sudo = false;
    private boolean echo = false;

    public ShellScriptWriter(Writer w) {
        this.out = new BufferedWriter(w);
    }

    public void setSudo(boolean sudo) {
        this.sudo = sudo;
    }

    public boolean isSudo() {
        return this.sudo;
    }

    public boolean isEcho() {
        return echo;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public void comment(String text) throws IOException {        
        out.write(String.format("# %s", text));
        out.newLine();
    }

    public void command(String cmd, String parameters) throws IOException {
        StringBuilder b = new StringBuilder();

        if (isSudo()) {
            b.append("sudo ");
        }

        b.append(cmd);
        b.append(" ");
        if (parameters != null) {
            b.append(parameters);
        }

        String line = b.toString();

        if (this.echo) {
            out.write(String.format("echo \'%s\'", line));
            out.newLine();
        }
        out.write(line);
        out.newLine();
    }

    protected String param(String option, Object value) {
        if (value == null) return "";

        if (value instanceof Invertable) {
            Invertable i = (Invertable)value;
            if (i.invert()) {
                return String.format(" ! %s %s", option, String.valueOf(i));
            }
        }

        return String.format(" %s %s", option, String.valueOf(value));
    }

    public void iptables(Chain chain, NamedProtocol p, String states, String intfIn, String intfOut, Object srcNet, PortSpec srcPort, Object dstNet, PortSpec dstPort, Action action, String ext) throws IOException {
        StringBuilder b = new StringBuilder();

        boolean multiport = false;

        b.append("-A ");        
        b.append(chain.name());

        switch (chain) {
            case PREROUTING:
            case POSTROUTING:
                b.append(" -t nat");
                break;
            
            default:
                break;
        }

        if (states != null) {
            b.append(" -m state --state ");
            b.append(states);
        }

        b.append(param("-p", p == null ? null : p.name()));

        if (chain != Chain.POSTROUTING) {
            b.append(param("-i", intfIn));
        }

        if (chain != Chain.PREROUTING) {
            b.append(param("-o", intfOut));
        }

        b.append(param("-s", srcNet));

        if (srcPort != null) {
            if (srcPort.isRange()) {
                b.append(" --match multiport --sports ");
                multiport = true;

                b.append(srcPort.getFrom());
                b.append(":");
                b.append(srcPort.getTo());
            } else {
                b.append(" --sport ");
                b.append(srcPort.getFrom());
            }
        }

        b.append(param("-d", dstNet));

        if (dstPort != null) {
            if (dstPort.isRange()) {
                if (!multiport) {   
                    b.append(" --match multiport --dports ");
                    multiport = true;
                } else {
                    b.append(" --dports ");
                }

                b.append(dstPort.getFrom());
                b.append(":");
                b.append(dstPort.getTo());
            } else {
                b.append(" --dport ");
                b.append(dstPort.getFrom());
            }
        }

        b.append(" -j ");
        b.append(action.name());

        if (ext != null) {
            b.append(" ");
            b.append(ext);
        }

        command("iptables", b.toString());
    }    

    public void newLine() throws IOException  {
        out.newLine();
    }

    public void flush() throws IOException {
        this.out.flush();
    }
}
