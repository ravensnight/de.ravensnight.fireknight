package de.ravensnight.fireknight;

import de.ravensnight.fireknight.common.Feature;
import de.ravensnight.fireknight.util.Receiver;
import de.ravensnight.fireknight.v2.parser.Knight;
import de.ravensnight.fireknight.v2.IPTablesGenerator;
import de.ravensnight.fireknight.v2.model.Script;
import de.ravensnight.fireknight.v2.model.ScriptBuilder;

import java.io.OutputStreamWriter;
import java.net.URL;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class Parser2Test 
{

    @Test
    public void test2() throws Exception
    {
        URL url = Parser2Test.class.getResource("/rules2.knight");

        Knight k = new Knight(url.openStream());

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {

            @Override
            public void receive(Script object) {

                try {
                    OutputStreamWriter w = new OutputStreamWriter(System.out);
                    IPTablesGenerator gen = new IPTablesGenerator(w);

                    gen.enable(Feature.SUDO, Feature.DROP_SUSPECT, Feature.ECHO);
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
        URL url = Parser2Test.class.getResource("/rules2.knight");

        Knight k = new Knight(url.openStream());

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {

            @Override
            public void receive(Script object) {

                try {
                    OutputStreamWriter w = new OutputStreamWriter(System.out);
                    IPTablesGenerator gen = new IPTablesGenerator(w);

                    gen.enable(Feature.ACCEPT_ESTABLISHED_RELATED, Feature.CLEANUP, Feature.LOG_SUSPECT, Feature.SUDO);
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
    public void testExample() throws Exception
    {
        URL url = Parser2Test.class.getResource("/example.knight");

        Knight k = new Knight(url.openStream());

        ScriptBuilder b = new ScriptBuilder(new Receiver<Script>() {

            @Override
            public void receive(Script object) {

                try {
                    OutputStreamWriter w = new OutputStreamWriter(System.out);
                    IPTablesGenerator gen = new IPTablesGenerator(w);

                    gen.enable(Feature.ACCEPT_ESTABLISHED_RELATED, Feature.CLEANUP, Feature.LOG_SUSPECT, Feature.DROP_SUSPECT, Feature.SUDO);
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
