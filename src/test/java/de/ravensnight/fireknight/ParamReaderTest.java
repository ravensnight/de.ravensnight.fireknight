package de.ravensnight.fireknight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.ravensnight.fireknight.util.ParamReader;
import de.ravensnight.fireknight.util.ParamHandler;

public class ParamReaderTest implements ParamHandler {

    private String value = null;

    enum Options {
        test, flag;
    }

    @Test
    public void test1() throws Exception {
        ParamReader r = new ParamReader(this);

        this.value = null;
        r.bind(Options.test).shortKey('t').build();

        String[] params = new String[] { "-t", "test1" };
        r.parse(params);

        assertNotNull(this.value);
        assertEquals(this.value, "test1");
    }

    @Test
    public void test2() throws Exception {
        ParamReader r = new ParamReader(this);

        this.value = null;
        r.bind(Options.test).longKey("test").build();

        String[] params = new String[] { "--test", "test2" };
        r.parse(params);

        assertNotNull(this.value);
        assertEquals(this.value, "test2");
    }

    @Test
    public void test3() throws Exception {
        ParamReader r = new ParamReader(this);

        this.value = null;
        r.bind(Options.test).longKey("test").shortKey('t').build();

        String[] params = new String[] { "-r", "--test", "test3" };
        r.parse(params);

        assertNotNull(this.value);
        assertEquals(this.value, "test3");
    }

    @Test
    public void test4() throws Exception {
        ParamReader r = new ParamReader(this);

        this.value = "test";
        r.bind(Options.flag).flag().shortKey('r').longKey("receive").build();

        String[] params = new String[] { "-r", "--test", "test3" };
        r.parse(params);

        assertNotNull(this.value);
        assertEquals(this.value, "flag");
    }

    @Override
    public void handleFlag(Object key) {
        if (Options.flag.equals(key)) {
            this.value = "flag";
        }
    }

    @Override
    public void handleParam(Object key, String param) {
        if (Options.test.equals(key)) {
            this.value = param;
        }
    }

}
