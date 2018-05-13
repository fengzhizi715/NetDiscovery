package com.cv4j.netdiscovery.admin.common;

import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.proxy.ProxyListPageParser;
import com.google.gson.JsonObject;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class CommonUtil {

//    public static void main(String[] args) {
//        Set<Class<?>> allClasses = getClassesFromPackage("com.cv4j.proxy.site");
//        Set<Class<?>> classes = getClassesByInterface(ProxyListPageParser.class, allClasses);
//        Iterator iterator = classes.iterator();
//        while (iterator.hasNext()) {
//            Object object = iterator.next();
//            String classPath = object.toString();
//            System.out.println(classPath.substring(classPath.lastIndexOf(".")+1));
//        }
//    }

    public static Set<Class<?>> getClassesFromPackage(String pack) {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    log.info("from package");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    log.info("from jar");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(
                                                packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + ‘.‘ + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    public static Set<Class<?>> getClassesByInterface(Class clazz, Set<Class<?>> classesAll) {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        //获取指定接口的实现类
        if (clazz.isInterface()) {
            try {
                /**
                 * 循环判断路径下的所有类是否继承了指定类
                 * 并且排除父类自己
                 */
                Iterator<Class<?>> iterator = classesAll.iterator();
                while (iterator.hasNext()) {
                    Class<?> cls = iterator.next();
                    /**
                     * isAssignableFrom该方法的解析，请参考博客：
                     * http://blog.csdn.net/u010156024/article/details/44875195
                     */
                    if (clazz.isAssignableFrom(cls)) {
                        if (!clazz.equals(cls)) {//自身并不加进去
                            classes.add(cls);
                        } else {

                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("出现异常");
            }
        }
        return classes;
    }

    public static HashMap getProxyPageMap(JobResource jobResource) {
        HashMap<String, Class> proxyMap = new HashMap<>();
        for (int i = jobResource.getStartPage(); i <= jobResource.getEndPage(); i++) {
            String url = null;
            if(Preconditions.isNotBlank(jobResource.getUrlSuffix())) {
                url = jobResource.getUrlPrefix() + "/" + i + "." +jobResource.getUrlSuffix();
            } else {
                url = jobResource.getUrlPrefix() + "/" + i;
            }

            log.info("url = "+url);
            try {
                proxyMap.put(url, Class.forName(jobResource.getParserClassPath()).newInstance().getClass());
            } catch (Exception e) {
                e.printStackTrace();;
            }
        }

        return proxyMap;
    }

    public static HttpEntity getHttpEntityForRequest(String jsonString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(jsonString, headers);
    }

    public static HttpEntity getHttpEntityForRequest(JsonObject jsonObject) {
        return getHttpEntityForRequest(jsonObject.toString());
    }
}