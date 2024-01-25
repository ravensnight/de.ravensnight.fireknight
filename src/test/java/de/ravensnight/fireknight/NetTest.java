package de.ravensnight.fireknight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.ravensnight.fireknight.v2.model.Net;
import de.ravensnight.fireknight.v2.model.NetParseException;

public class NetTest {

    @Test
    public void parseTest1() throws NetParseException {
        String s = "127.0.0.1";
        Net n = Net.parse(s);

        assertNotNull(n.getAddr());
        assertEquals(null, n.getMask());
        assertEquals(0x7F000001L, n.getAddr().longValue());
    }

    @Test
    public void parseTest2() throws NetParseException {
        String s = "127.0.0.1";
        Net n = Net.parse(s);

        assertEquals("127.0.0.1", n.toString());
    }

    @Test
    public void parseTest3() throws NetParseException {
        String s = "192.168.66.1/32";
        Net n = Net.parse(s);

        assertNotNull(n.getAddr());
        assertNotNull(n.getMask());
        assertEquals(0xC0A84201, n.getAddr().longValue());
        assertEquals(32, n.getMask().intValue());
    }

    @Test
    public void parseTest4() throws NetParseException {
        String s = "192.168.66.1/24";
        Net n = Net.parse(s);

        assertEquals("192.168.66.0/24", n.toString());
    }
}
