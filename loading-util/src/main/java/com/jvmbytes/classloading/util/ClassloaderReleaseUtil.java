package com.jvmbytes.classloading.util;

import sun.misc.SunClassLoaderUtil;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarFile;

/**
 * @author wongoo
 */
public class ClassloaderReleaseUtil {

    /**
     * 尽可能关闭ClassLoader
     * <p>
     * URLClassLoader会打开指定的URL资源，在SANDBOX中则是对应的Jar文件，如果不在shutdown的时候关闭ClassLoader，会导致下次再次加载
     * 的时候，依然会访问到上次所打开的文件（底层被缓存起来了）
     * <p>
     * 在JDK1.7版本中，URLClassLoader提供了{@code close()}方法来完成这件事；但在JDK1.6版本就要下点手段了；
     * <p>
     * 该方法将会被{@code ControlModule#shutdown}通过反射调用，
     * 请保持方法声明一致
     */
    public static void closeIfPossible(ClassLoader loader) {
        System.out.println("try close class loader: " + loader);

        // 如果是JDK7+的版本, URLClassLoader实现了Closeable接口，直接调用即可
        if (loader instanceof Closeable) {
            try {
                final Method closeMethod = URLClassLoader.class.getMethod("close");
                System.out.println("call classloader.close()");
                closeMethod.invoke(loader);
            } catch (Throwable cause) {
                System.out.println("classloader close, " + cause.getClass().getName() + ":" + cause.getMessage());
            }
            return;
        }


        if (loader.getClass().isAssignableFrom(URLClassLoader.class)) {
            System.out.println("release url class loader");

            URLClassLoader urlClassLoader = (URLClassLoader) loader;
            SunClassLoaderUtil.releaseLoader(urlClassLoader);
            return;
        }

        // 对于JDK6的版本，URLClassLoader要关闭起来就显得有点麻烦，这里弄了一大段代码来稍微处理下
        // 而且还不能保证一定释放干净了，至少释放JAR文件句柄是没有什么问题了
        try {
            final Object sunMiscURLClassPath = URLClassLoader.class.getDeclaredField("ucp").get(loader);
            final Object javaUtilCollection = sunMiscURLClassPath.getClass().getDeclaredField("loaders").get(sunMiscURLClassPath);

            for (Object sunMiscURLClassPathJarLoader :
                    ((Collection) javaUtilCollection).toArray()) {
                try {
                    final JarFile javaUtilJarJarFile = (JarFile) sunMiscURLClassPathJarLoader.getClass().getDeclaredField("jar").get(sunMiscURLClassPathJarLoader);
                    javaUtilJarJarFile.close();
                } catch (Throwable t) {
                    // if we got this far, this is probably not a JAR loader so skip it
                }
            }

        } catch (Throwable cause) {
            System.out.println("classloader release, " + cause.getClass().getName() + ":" + cause.getMessage());
        }

    }


}
