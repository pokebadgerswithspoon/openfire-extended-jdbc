/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.pokebadgerswithspoon.openfire.auth;

import io.github.pokebadgerswithspoon.openfire.auth.PasswordChecker;
import io.github.pokebadgerswithspoon.openfire.auth.DovecotPasswordChecker;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author lauri
 */
public class DovecotPasswordCheckerTest {
   
    @Test
    public void testObfuscate() {
        PasswordChecker passwordChecker =  new DovecotPasswordChecker();
        String obfuscated = passwordChecker.obfuscate("password");
        Assert.assertTrue(
                passwordChecker.matches("password", obfuscated)
        );
    }
    
    
    @Test
    public void testDigestFetch() {
        DovecotPasswordChecker checker = new DovecotPasswordChecker();
        String password = "$1$847919e0$f1c42145e0d5508fed5cbbc2183d2e63";
        Assert.assertEquals("$1$", checker.fetchDigestPrefix(password));
        Assert.assertEquals("MD5", checker.fetchDigest(checker.fetchDigestPrefix(password)));
    }
    
    @Test
    public void testSaltFetch() {
        DovecotPasswordChecker checker = new DovecotPasswordChecker();
        String password = "$1$847919e0$f1c42145e0d5508fed5cbbc2183d2e63";
        Assert.assertEquals("847919e0", checker.fetchSalt(password));
    }
}
