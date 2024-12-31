package cn.butler.payloads.config;

import org.apache.commons.cli.*;

import java.util.Map;

import static cn.butler.GeneratePayload.*;

public abstract class Config {

    public static CommandLine helpCmdLine;
    public static CommandLine modeCmdLine;
    protected static final int INTERNAL_ERROR_CODE = 70;
    protected static final int USAGE_CODE = 64;
    protected static final String VERSION = "1.0.1";

    public abstract void parse(CommandLine cmdLine);

    public static void parse(Map<String, Options> allOptions, final String[] args){
        CommandLineParser parser = new DefaultParser();
        //解析help帮助参数
        Options helpOptions = allOptions.get("help");
        try {
            helpCmdLine = parser.parse(helpOptions, args,true); //宽松模式解析
            // 处理帮助命令
            if (helpCmdLine.hasOption("help")) {
                String mode = helpCmdLine.getOptionValue("h");
                Options options = allOptions.get(mode);
                System.out.println(String.format("Help for module: %s",mode));
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(String.format("java -jar ysoSimple.jar -m %s",mode), options);
                System.exit(USAGE_CODE);
            }

            if(args.length == 0){
                printUsage(helpOptions);
                System.exit(USAGE_CODE);
            }

            if(helpCmdLine.hasOption("mode")){
                String mode = helpCmdLine.getOptionValue("mode");
                Options options = allOptions.get(mode);
                modeCmdLine = parser.parse(options,args,true);
                if(mode.equals("HessianAttack")){
                    hessianConfig.parse(modeCmdLine);
                }else if (mode.equals("JNDIAttack")) {
                    jndiConfig.parse(modeCmdLine);
                }else if (mode.equals("YsoAttack")){
                    ysoConfig.parse(modeCmdLine);
                } else if (mode.equals("XStreamAttack")) {
                    xStreamConfig.parse(modeCmdLine);
                } else if (mode.equals("SnakeYamlAttack")) {
                    snakeYamlConfig.parse(modeCmdLine);
                } else if (mode.equals("JdbcAttack")) {
                    jdbcAttackConfig.parse(modeCmdLine);
                } else if (mode.equals("SSTIAttack")) {
                    sstiAttackConfig.parse(modeCmdLine);
                } else if (mode.equals("ThirdPartyAttack")) {
                    thirdPartyConfig.parse(modeCmdLine);
                } else {
                    printUsage(options);
                    System.exit(USAGE_CODE);
                }
            }else {
                printUsage(helpOptions);
                System.exit(USAGE_CODE);
            }

        }catch (Exception e){
            System.out.println("[*] Parameter input error, Please use -h or -help for more  usage information");
            printUsage(helpOptions);
        }
    }

    /**
     * todo 优化Config配置下使用方式的输出
     * @param options
     */
    public static void printUsage(Options options){
        System.out.println(String.format("[*] ysoSimple v%s   ",VERSION));

        new HelpFormatter().printHelp("ysoSimple.jar", options, true);
        System.out.println("Use the -help command and refer to the documentation for more usage instructions:");
        System.out.println("java -jar ysoSimple.jar -help JNDIAttack/YsoAttack/HessianAttack/JDBCAttack");
        System.exit(0);
    }
}
