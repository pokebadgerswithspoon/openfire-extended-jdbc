/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.pokebadgerswithspoon.openfire.auth;

import io.github.pokebadgerswithspoon.openfire.auth.ExtendedJdbcAuthProvider;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.junit.Test;

/**
 *
 * @author lauri
 */
public class ExtendedJdbcAuthProviderTests {
    @Test(expected = UnauthorizedException.class)
    public void testInit() throws UnauthorizedException {
        ExtendedJdbcAuthProvider provider = new ExtendedJdbcAuthProvider();
        provider.authenticate("admin", "1234");
    }
}
