package com.sys1yagi.fragmentcreator.testtool;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;

// see more https://github.com/permissions-dispatcher/PermissionsDispatcher/blob/master/processor/src/test/java/permissions/dispatcher/processor/base/AndroidAwareClassLoader.java
public final class AndroidAwareClassLoader {

    private static final String TEST_CLASSPATH_FILE_NAME = "/additional-test-classpath.txt";

    private AndroidAwareClassLoader() {
        throw new AssertionError();
    }

    public static ClassLoader create() {
        try {
            InputStream stream = AndroidAwareClassLoader.class.getResourceAsStream(TEST_CLASSPATH_FILE_NAME);
            URL[] urls = IOUtils.readLines(stream, Charset.forName("UTF-8"))
                .stream()
                .map(AndroidAwareClassLoader::unsafeToURL)
                .toArray(URL[]::new);

            return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL unsafeToURL(String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
