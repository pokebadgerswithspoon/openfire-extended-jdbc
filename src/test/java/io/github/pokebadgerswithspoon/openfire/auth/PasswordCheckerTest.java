/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.pokebadgerswithspoon.openfire.auth;

import io.github.pokebadgerswithspoon.openfire.auth.PasswordChecker;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lauri
 */
public class PasswordCheckerTest {

    @Test
    public void testPlain() {
        PasswordChecker checker = new PasswordChecker.Factory().create("plain");
        assertNotNull(checker);
        assertEquals("password", checker.obfuscate("password"));
        assertTrue(checker.matches("password", "password"));
    }

    @Test
    public void testMD5() {
        PasswordChecker checker = new PasswordChecker.Factory().create("md5");
        assertNotNull(checker);
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", checker.obfuscate("password"));
        assertTrue(checker.matches("password", "5f4dcc3b5aa765d61d8327deb882cf99"));
    }

    @Test
    public void testCheckersKnownToOpenfire() {
        String[] names = "plain,md5,sha1,sha256,sha512".split(",");
        for (String name : names) {
            PasswordChecker checker = new PasswordChecker.Factory().create(name);
            assertNotNull(checker);
        }
    }

    @Test
    public void testDigestCheckerBuilder() {
        String[] names = "digest:MD5,digest:SHA-1".split(",");
        for (String name : names) {
            PasswordChecker checker = new PasswordChecker.Factory().create(name);
            assertNotNull(checker);
        }
    }
}
