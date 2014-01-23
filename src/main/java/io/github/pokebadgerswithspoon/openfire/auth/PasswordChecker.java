package io.github.pokebadgerswithspoon.openfire.auth;

import java.util.Map;
import java.util.HashMap;
import org.jivesoftware.util.StringUtils;

/**
 * Iterface to generate password hashes and check passwords against generated hashes.
 * Idea is to detach hash algorythms from original {@link org.jivesoftware.openfire.auth.JDBCAuthProvider}.
 * 
 * @author lauri
 */
public interface PasswordChecker {

    boolean matches(String userPassword, String storedPassword);

    String obfuscate(String password);

    static class Plain implements PasswordChecker {

        @Override
        public boolean matches(String userPassword, String storedPassword) {
            return obfuscate(userPassword).equals(storedPassword);
        }

        @Override
        public String obfuscate(String password) {
            return password;
        }
    }

    static class DigestChecker extends Plain {

        private final String algorythm;

        public DigestChecker(String algorythm) throws IllegalArgumentException {
            if (algorythm == null) {
                throw new IllegalArgumentException("Algorythm can not be null");
            }
            this.algorythm = algorythm;
        }

        @Override
        public String obfuscate(String s) {
            return StringUtils.hash(s, algorythm);
        }
    }

    public static class Factory {

        private static final Map<String, PasswordChecker> implMap;
        public static final String PLAIN = "plain";

        static {
            implMap = new HashMap();
            implMap.put(PLAIN, new Plain());
            implMap.put("md5", new DigestChecker("MD5"));
            implMap.put("sha1", new DigestChecker("SHA-1"));
            implMap.put("sha256", new DigestChecker("SHA-256"));
            implMap.put("sha512", new DigestChecker("SHA-512"));
        }

        public PasswordChecker create(String name) {
            PasswordChecker impl = implMap.get(name);
            if (impl == null) {
                if (name.startsWith("digest:")) {
                    return createDigestChecker(name);
                } else {
                    return createClassChecker(name);
                }
            }
            return impl;
        }

        protected PasswordChecker createClassChecker(String name) {
            try {
                return (PasswordChecker) Class.forName(name).newInstance();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Unable to initialize password checker", ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException("Unable to initialize password checker", ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Unable to initialize password checker", ex);
            }
        }

        protected PasswordChecker createDigestChecker(String name) {
            if (name.startsWith("digest:")) {
                name = name.substring(7);
            }
            return new DigestChecker(name);
        }
    }
}
