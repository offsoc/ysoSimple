package cn.butler.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    // 将 Payload 保存到指定文件路径中
    public static void savePayloadToFile(String payload, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();  // 确保目录存在
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(payload);
            System.out.println("Serialized Data has been saved to " + filePath);
        }
    }

    public static void saveBytePayloadToFile(byte[] payload, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();  // 确保目录存在
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(payload);
            System.out.println("Serialized Data has been saved to " + filePath);
        }
    }
}
