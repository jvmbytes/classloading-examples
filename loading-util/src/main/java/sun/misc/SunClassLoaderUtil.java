//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package sun.misc;

import sun.misc.URLClassPath.JarLoader;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarFile;

public class SunClassLoaderUtil {
    public SunClassLoaderUtil() {
    }

    public static void releaseLoader(URLClassLoader var0) {
        releaseLoader(var0, (List) null);
    }

    public static List<IOException> releaseLoader(URLClassLoader loader, List<String> paths) {
        LinkedList exceptions = new LinkedList();

        try {
            if (paths != null) {
                paths.clear();
            }

            URLClassPath path = SharedSecrets.getJavaNetAccess().getURLClassPath(loader);
            ArrayList loaders = path.loaders;
            Stack urls = path.urls;
            HashMap lmap = path.lmap;
            synchronized (urls) {
                urls.clear();
            }

            synchronized (lmap) {
                lmap.clear();
            }

            synchronized (path) {
                Iterator iterator = loaders.iterator();

                while (true) {
                    Object o;
                    do {
                        do {
                            if (!iterator.hasNext()) {
                                loaders.clear();
                                return exceptions;
                            }

                            o = iterator.next();
                        } while (o == null);
                    } while (!(o instanceof JarLoader));

                    JarLoader jarLoader = (JarLoader) o;
                    JarFile jarFile = jarLoader.getJarFile();

                    try {
                        if (jarFile != null) {
                            jarFile.close();
                            if (paths != null) {
                                paths.add(jarFile.getName());
                            }
                        }
                    } catch (IOException e) {
                        String message = jarFile == null ? "filename not available" : jarFile.getName();
                        String error = "Error closing JAR file: " + message;
                        IOException exception = new IOException(error);
                        exception.initCause(e);
                        exceptions.add(exception);
                    }
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
