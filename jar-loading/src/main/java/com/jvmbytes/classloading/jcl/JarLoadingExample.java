package com.jvmbytes.classloading.jcl;

import com.jvmbytes.classloading.util.ClassloaderReleaseUtil;
import org.xeustechnologies.jcl.JarClassLoader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author wongoo
 */
public class JarLoadingExample {


    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("parameter required");
            return;
        }

        String jarPath = args[0];
        String className = args[1];

        System.out.println("jar: " + jarPath);
        System.out.println("class: " + className);

        urlLoading(jarPath, className);
        jclLoading(jarPath, className);
    }


    private static void urlLoading(String jarPath, String className) throws Exception {
        System.out.println("-----------------");
        System.out.println("url loading start");

        URL url = new URL("file:///" + jarPath);
        URLClassLoader loader = new URLClassLoader(new URL[]{url});

        loader.loadClass(className);
        Class clazz = loader.loadClass(className);
        if (clazz == null) {
            System.out.println("class not found");
            return;
        }

        // Object user = clazz.newInstance();
        ClassloaderReleaseUtil.closeIfPossible(loader);
        // System.out.println(user);

        System.out.println("url loading end");
    }

    private static void jclLoading(String jarPath, String className) throws Exception {
        System.out.println("-----------------");
        System.out.println("jcl loading start");

        JarClassLoader loader = new JarClassLoader();
        loader.add(jarPath);

        Class clazz = loader.loadClass(className);
        if (clazz == null) {
            System.out.println("class not found");
            return;
        }

        Object user = clazz.newInstance();

        loader.unloadClass(className);

        System.out.println(user);

        ClassloaderReleaseUtil.closeIfPossible(loader);
        System.out.println(user);

        System.out.println("jcl loading end");
    }
}
