package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.payloads.annotation.Dependencies;

@Dependencies( { "com.mchange:c3p0:0.9.5.2" ,"com.mchange:mchange-commons-java:0.2.11"} )
public class C3P01_c3p0 implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }

    public static void main ( String[] args ) throws Exception {
        PayloadRunner.run(C3P01_c3p0.class, args);
    }
}
