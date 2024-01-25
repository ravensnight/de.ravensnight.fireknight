package de.ravensnight.fireknight;

import de.ravensnight.fireknight.common.Feature;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.v1.parser.Knight;
import de.ravensnight.fireknight.v1.IPTablesGenerator;
import de.ravensnight.fireknight.v1.model.AcceptRule;
import de.ravensnight.fireknight.v1.model.Scope;
import de.ravensnight.fireknight.v1.model.Script;
import de.ravensnight.fireknight.v1.model.ScriptBuilder;

import java.io.OutputStreamWriter;
import java.net.URL;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class Parser1Test 
{
    @Test
    public void test() throws Exception
    {
        URL url = Parser1Test.class.getResource("/rules.knight");

        Knight k = new Knight(url.openStream());

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {

            @Override
            public void receive(Script object) {
                System.out.println("Received script.");

                for (Scope s : object.getInterfaces()) {
                    System.out.println(s.getInterfaceName() + ":");
                    for (AcceptRule r : s.getAccept()) {
                        System.out.println(r);
                    }
                }
            }            
        });

        k.parse(b);
    }    

    @Test
    public void test2() throws Exception
    {
        URL url = Parser1Test.class.getResource("/rules.knight");

        Knight k = new Knight(url.openStream());

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {

            @Override
            public void receive(Script object) {

                try {
                    OutputStreamWriter w = new OutputStreamWriter(System.out);
                    IPTablesGenerator gen = new IPTablesGenerator(w);

                    gen.enable(Feature.SUDO, Feature.DROP_SUSPECT);
                    gen.write(object);
                    w.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }            
        });

        k.parse(b);
    }

    @Test
    public void test3() throws Exception
    {
        URL url = Parser1Test.class.getResource("/rules.knight");

        Knight k = new Knight(url.openStream());

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {

            @Override
            public void receive(Script object) {

                try {
                    OutputStreamWriter w = new OutputStreamWriter(System.out);
                    IPTablesGenerator gen = new IPTablesGenerator(w);

                    gen.enable(Feature.ACCEPT_ESTABLISHED_RELATED, Feature.CLEANUP, Feature.DROP_SUSPECT, Feature.LOG_SUSPECT);
                    gen.write(object);
                    w.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }            
        });

        k.parse(b);
    }

}
