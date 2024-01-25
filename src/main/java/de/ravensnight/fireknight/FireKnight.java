package de.ravensnight.fireknight;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import de.ravensnight.fireknight.common.Feature;
import de.ravensnight.fireknight.v2.GeneratorException;
import de.ravensnight.fireknight.v2.IPTablesGenerator;
import de.ravensnight.fireknight.v2.model.Script;
import de.ravensnight.fireknight.v2.model.ScriptBuilder;
import de.ravensnight.fireknight.v2.parser.Knight;
import de.ravensnight.fireknight.v2.parser.ParseException;
import de.ravensnight.fireknight.util.ParamReader;
import de.ravensnight.fireknight.util.ParamReaderException;
import de.ravensnight.fireknight.util.ParamHandler;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.util.ParamReader.ParamConfig;

public class FireKnight implements ParamHandler {
    
    private ParamReader params = null;
    private String inputFile = null;
    private String outputFile = null;

    private final Set<Feature> features = new HashSet<>();
    private boolean outputHelp = false;

    enum Parameter {
        acceptEstablishedRelated,
        sudo,
        cleanup,
        dropSuspect,
        logSuspect,
        echo,
        input,
        output,
        help
    }

    private FireKnight() {
    }

    public static FireKnight create() {
        FireKnight f = new FireKnight();
        f.init();

        return f;
    }

    protected void init() {

        this.params = new ParamReader(this);
        this.inputFile = null;
        this.outputFile = null;

        this.params.bind(Parameter.acceptEstablishedRelated).flag().shortKey('r').longKey(Parameter.acceptEstablishedRelated.name()).description("Generally allow established and related traffic").build();
        this.params.bind(Parameter.sudo).flag().shortKey('s').longKey(Parameter.sudo.name()).description("Prepend each command with 'sudo'").build();
        this.params.bind(Parameter.cleanup).flag().shortKey('c').longKey(Parameter.cleanup.name()).description("Add some cleanup commands in the IPtables script").build();
        this.params.bind(Parameter.dropSuspect).flag().shortKey('d').longKey(Parameter.dropSuspect.name()).description("Drop supect packages").build();
        this.params.bind(Parameter.logSuspect).flag().shortKey('l').longKey(Parameter.logSuspect.name()).description("Send supect packages to syslog").build();
        this.params.bind(Parameter.echo).flag().shortKey('e').longKey(Parameter.logSuspect.name()).description("Print each command to output.").build();

        this.params.bind(Parameter.input).shortKey('i').longKey(Parameter.input.name()).description("Set the input file with <value>").required().build();
        this.params.bind(Parameter.output).shortKey('o').longKey(Parameter.output.name()).description("Set an output file with <value>").build();

        this.params.bind(Parameter.help).flag().longKey("help").description("Print this help").build();
    }

    public void start(String[] parameters) throws IOException, ParseException {

        String paramError = null;

        try {
            this.params.parse(parameters);
        } catch (ParamReaderException e)  {
            paramError = e.getMessage();
            this.outputHelp = true;
        }

        if (paramError != null) {
            System.err.println(paramError);
        }

        if (this.outputHelp) {
            printHelp();
            return;
        }

        Writer w = null;
        if (this.outputFile == null) {
            w = new OutputStreamWriter(System.out);
        } else {
            w = new FileWriter(this.outputFile);
        }

        final IPTablesGenerator gen = new IPTablesGenerator(w);
        for (Feature f : this.features) {
            gen.enable(f);
        }

        FileReader r = new FileReader(this.inputFile);
        Knight knight = new Knight(r);

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {
            
            @Override
            public void receive(Script script) {
                try {
                    gen.write(script);
                } catch (IOException|GeneratorException e) {
                    System.err.println("Generation failed: " + e.getMessage());
                }
            }
            
        });

        knight.parse(b);
    }

    private void printHelp() {
        PrintWriter w = new PrintWriter(System.out);

        w.append("java -jar ");
        w.append("<fireknight.jar>");

        boolean hasShort = false;

        for (ParamConfig c : this.params.getParameters()) {
            if (!c.isRequired()) continue;

            w.append(" ");

            hasShort = false;
            if (c.getShortKey() != null) {
                w.append("-"); 
                w.append(c.getShortKey());
                hasShort = true;
            }

            if (c.getLongKey() != null) {
                if (hasShort) w.append("|");
                w.append("--"); 
                w.append(c.getLongKey());
            }

            if (!c.isFlag()) {
                w.append(" ");
                w.append("<value>");
            }
        }

        for (ParamConfig c : this.params.getParameters()) {
            if (c.isRequired()) continue;
        
            w.append(" [");
            hasShort = false;
            if (c.getShortKey() != null) {
                hasShort = true;
                w.append("-"); 
                w.append(c.getShortKey());
            }

            if (c.getLongKey() != null) {
                if (hasShort) w.append("|");
                w.append("--"); 
                w.append(c.getLongKey());
            }

            if (!c.isFlag()) {
                w.append(" ");
                w.append("<value>");
            }

            w.append("]");
        }

        w.append("\n");

        for (ParamConfig c : this.params.getParameters()) {
                    
            StringBuilder b = new StringBuilder();

            hasShort = false;
            if (c.getShortKey() != null) {
                hasShort = true;
                b.append("-"); 
                b.append(c.getShortKey());
            } else {
                b.append("   ");
            }

            if (c.getLongKey() != null) {
                if (hasShort) b.append("|");
                b.append("--"); 
                b.append(c.getLongKey());
            }

            w.append(String.format("\t%-30s - %s", b.toString(), c.getDescription()));
            w.append("\n");
        }

        w.flush();
    }

    /**
     * @param params
     */
    public static void main(String[] params) {

        FireKnight f = FireKnight.create();
        try {
            f.start(params);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void handleFlag(Object key) {
        Parameter p = (Parameter)key;
        switch (p) {
            case acceptEstablishedRelated:
                this.features.add(Feature.ACCEPT_ESTABLISHED_RELATED); 
                break;

            case cleanup:
                this.features.add(Feature.CLEANUP); 
                break;

            case dropSuspect:
                this.features.add(Feature.DROP_SUSPECT); 
                break;

            case logSuspect:
                this.features.add(Feature.LOG_SUSPECT); 
                break;

            case sudo:
                this.features.add(Feature.SUDO); 
                break;

            case echo:
                this.features.add(Feature.ECHO);
                break;

            case help:
                this.outputHelp = true;
                break;

            default:
                break;
        }
    }

    @Override
    public void handleParam(Object key, String param) {
        Parameter p = (Parameter)key;
        switch (p) {
            case input:
                this.inputFile = param;
                break;

            case output:
                this.outputFile = param;
                break;

            default:
                break;
        }
    }
}
