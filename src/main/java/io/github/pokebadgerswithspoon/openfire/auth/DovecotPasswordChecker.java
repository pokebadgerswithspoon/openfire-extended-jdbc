/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.pokebadgerswithspoon.openfire.auth;

import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.util.StringUtils;
import org.mortbay.jetty.security.UnixCrypt;

/**
 *
 * Initial support of passwords as it used by dovecot
 * {@linkplain http://wiki2.dovecot.org/Authentication/PasswordSchemes}
 *
 * @author lauri
 */
public class DovecotPasswordChecker implements PasswordChecker {

    private final static Map<String, String> digestByPrefix, prefixByDigest;

    static {
        digestByPrefix = new HashMap();
        digestByPrefix.put("$1$", "MD5");
        digestByPrefix.put("$5$", "SHA512");
        digestByPrefix.put("$6$", "SHA512");

        prefixByDigest = new HashMap();
        for (Map.Entry<String, String> e : digestByPrefix.entrySet()) {
            prefixByDigest.put(e.getValue(), e.getKey());
        }
    }

    public boolean matches(String userPassword, String saltedPassword) {
        String prefix = fetchDigestPrefix(saltedPassword);
        String storedSalt = fetchSalt(saltedPassword);

        String calculatedPass = obfuscate(prefix, storedSalt, userPassword);

        return saltedPassword.equals(calculatedPass);
    }

    String fetchDigestPrefix(String password) {
        for (String prefix : digestByPrefix.keySet()) {
            if (password.startsWith(prefix)) {
                return prefix;
            }
        }
        return "";
    }

    String fetchDigest(String prefix) {
        if ("".equals(prefix)) {
            return "MD5";
        } else {
            return digestByPrefix.get(prefix);
        }
    }

    String fetchSalt(String password) {
        return password.substring(3, password.indexOf("$", 3));
    }

    public String obfuscate(String password) {
        String digest = "MD5";
        String prefix = prefixByDigest.get(digest);
        String salt = StringUtils.hash(new Double(Math.random()).toString(), digest).substring(0, 8);

        return obfuscate(prefix, salt, password);
    }

    protected String obfuscate(String prefix, String salt, String password) {
        return new StringBuilder()
                .append(prefix)
                .append(salt)
                .append("$")
                .append(UnixCrypt.crypt(password, prefix+salt))
                .toString();
    }
}
