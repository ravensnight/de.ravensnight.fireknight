package de.ravensnight.fireknight;

import org.junit.Test;

public class FireKnightTest {

    @Test
    public void test() throws Exception {

        String[] params = new String[] {
            "--acceptEstablishedRelated", "--sudo", "--cleanup", "--logSuspect", "--dropSuspect",
            "--input ", "./src/test/resources/rules.knight"
        };

        FireKnight.main(params);
    }

    @Test
    public void help() throws Exception {

        String[] params = new String[] {
            "--help"
        };

        FireKnight.main(params);
    }
}
