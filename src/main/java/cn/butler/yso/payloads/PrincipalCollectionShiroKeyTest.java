package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.payloads.annotation.Authors;
import cn.butler.payloads.annotation.Dependencies;
import org.apache.shiro.subject.SimplePrincipalCollection;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"shiro-key:shiro-key:1.2.4"})
@Authors({ Authors.B0T1ER })
public class PrincipalCollectionShiroKeyTest implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection();
        return simplePrincipalCollection;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(PrincipalCollectionShiroKeyTest.class, args);
    }
}
