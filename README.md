# ysoSimple

ysoSimple：简易的Java漏洞利用工具，集成Java反序列化，Hessian反序列化，XStream反序列化，SnakeYaml反序列化，Shiro550，JSF反序列化，SSTI模板注入，JdbcAttackPayload，JNDIAttack，字节码生成。

# 0x01 前言:

## a.源起:

ysoSimple工具是基于[ysoserial-for-woodpecker](https://github.com/woodpecker-framework/ysoserial-for-woodpecker)和[JNDIMap](https://github.com/X1r0z/JNDIMap)二开的Java利用工具，主要是方便自己学习漏洞利用和方便自己整理漏洞利用。

其实网上已经有很多优秀的Java漏洞利用工具，比如像wh1t31p师傅的ysomap，c0ny1师傅的ysoerial-for-woodpecker，qi4l师傅的JYso，X1r0z师傅的JNDIMap，Ar3h师傅超强的web-chains。为什么还要自己搞个工具造轮子？主要有以下的原因：

1. 学习师傅们Java利用链的编写思路，用来自我提升。
2. 方便自己快速添加想要的功能，在实战中自主修改并快速使用。
3. 打造属于自己的工具箱，把自己想整理的漏洞链利用存起来。
4. 某些系统应用会有自己的反序列化链和协议，需要自己整理工具箱。
5. 完成毕业设计(emmm...)

即使魔改的工具不如这几位前辈的，但是本着自我学习和能力提升的想法还是给自己挖坑。ysoSimple工具目前是命令行格式的，方便使用修改。因为我的代码水平不强，所以都是缝在一起的。
工具还有很多地方不完善，后续慢慢整理补充.......

## b.漏洞利用思路:

一般像工具的使用通常都是在黑盒情况下，黑盒测试遇到反序列化和JNDI注入情况时：首先我会用像dns/sleep这种poc进行测试来确系漏洞存在，如果漏洞利用方式支持对目标环境进行探测我会先把目标环境的中间件/JDK版本/操作系统这些信息摸清楚方便后续深入利用，然后在进行漏洞利用链攻击时会打下dnslog/sleep方便确认漏洞利用链肯定能使用，最后就是内存马/反弹Shell这些了。

### (1)Java反序列化

当黑盒测试Java反序列化漏洞时，从环境探测到漏洞利用思路：

1. URIDNS/FindClassByBomb测试Java反序列化漏洞是否存在
2. FindClassByDNS/FindGadgetByDNS/FindClassByBomb测试目标环境利用链，中间件，操作系统，jdk版本
3. CommonsBeautils/CommonsCollections/C3P0漏洞利用(dnslog测试，sleep延迟，回显测试，内存马，反弹Shell)
4. dirt-data-length/UTF8-Overlong Encoding对抗WAF

### (2)Hessian反序列化

当黑盒测试Hessian反序列化漏洞时，从环境探测到漏洞利用思路：

1. LazyValue-InetAddress测试Hessian反序列化漏洞是否存在(用Hessian1和Hessian2协议都打下)
2. LazyValue-BCELLoader/LazyValue-Jndi/LazyValue-XSTL漏洞利用(dnslog测试，sleep延迟，回显测试，内存马，反弹Shell)
3. dirt-data-length/UTF8-Overlong Encoding对抗WAF

### (3)XStram反序列化

1. FindClassByBomb测试XStreasm反序列化漏洞是否存在
2. .....

### (4)SnakeYaml反序列化

1. FindClassByDNS测试SnakeYaml反序列化漏洞是否存在
2. FindClassByDNS探测目标环境中间件，操作系统，jdk版本
3. JdbcRowImpl/ScriptEngineManager/C3P0链漏洞利用

### (5)JNDI注入

JNDI系列打法分为RMI注入和JNDI注入：

**RMI注入**

1. JDK低版本远程工厂类加载(dnslog测试，sleep延迟，回显测试，内存马，反弹Shell)
2. JDK高版本本地工厂类利用(BeanFactory单参数RCE打法，JDBC Factory数据库连接打法...)
3. JRMPListener JRMP层的攻击

**JNDI注入**

1. JDK低版本远程工厂类加载(dnslog测试，sleep延迟，回显测试，内存马，反弹Shell)
2. JDK高版本本地工厂类利用(BeanFactory单参数RCE打法，JDBC Factory数据库连接打法...)
3. ldap打Java反序列化(用的多)，这部分就和上面Java反序列化漏洞利用思路一样了

### (6)SSTI模板注入

Java系列的模板：Freemarker，Velocity，Pebble。模板注入都是间接操作JavaAPI，通常没有探测环境的步骤，直接测试漏洞利用，利用时同样是先测试(dnslog测试，sleep延迟)保证利用链可以被利用。

**FreeMarker**

* 内建函数?new：Execute命令执行，JythonRuntime命令执行，ObjectConstructor实例化对象
* 内建函数?api：调用对象的方法
* StaticModel：静态方法调用
* dataModel：数据模型(FreeMarker<2.3.30绕过沙箱)
* springMacroRequestContext Request上下文RCE

### (7)JdbcAttack数据库利用

在系统管理后台，包括Fastjson/Java反序列化都可能涉及到JDBCAttack。JDBCAttack整理出来有俩种利用方式: 连接串利用，执行SQL语句利用。

**H2 DataBase：数据库连接串漏洞利用**

* H2 CreateAlias 执行Java代码
* H2 RunScript 远程加载SQL文件
* H2 StaticMethod 执行Java静态语法
* H2 Groovy 执行Groovy表达式
* H2 JavaScript 执行JS表达式

**Derby：执行SQL语句漏洞利用**

* 直接加载java字节码RCE
* 远程加载jar包
* 落地jar包加载

**PostgreSQL：数据库连接漏洞利用**

* socketFactory/socketFactoryArg 单参数构造方法实例化
* loggerLevel/loggerFile 日志文件写入

**MySQL：数据库连接漏洞利用**

* ReadFile：读文件
* detectCustomCollations：触发Java反序列化，测试步骤和上面一样
* ServerStatusDiffInterceptor：触发Java反序列化，测试步骤和上面一样

# 0x02 简单使用

## a.支持的模块

目前ysoSimple集成7种漏洞利用模块：

1. Java原生反序列化：YsoAttack
2. Hessian反序列化：HessianAttack
3. XStram反序列化：XStramAttack
3. SnakeYaml反序列化：SnakeYamlAttack
4. Shiro550反序列化：YsoAttack
5. JSF反序列化：YsoAttack
4. JdbcAttack模块：JdbcAttack
5. SSTI模板注入：SSTIAttack
6. JNDI服务器：JNDIAttack
7. 字节码生成模块：ThirdPartyAttack

YsoSimple用-m或者-mode参数选择要使用的模块，例如：

```bash
java -jar ysoSimple.jar -m YsoAttack
```

除JNDIAttack外其余模块都需要俩个必要参数-g(-gadget)和-a(-args)，-gadget指定生成的利用链。-args指定该利用链的漏洞利用效果，例如：

```bash
java -jar ysoSimple.jar -m YsoAttack -g CommonsBeanutils2 -a "Templateslmpl:dnslog:whoami.dnslog.cn"
```

每个模块除-g(-gadget)和-a(-args)俩个参数之外每个模块还有额外的参数，这些参数用于对利用链进行修饰(压缩/混淆/编码/存文件)。在wiki的模块介绍中都会详细说明。

JNDIAttack模块启动

```bash
java -jar ysoSimple.jar -m JNDIAttack
```

## b.help命令

使用help命令然后跟着模块名会有简单的使用方式说明，结合文档来看会更好。

```bash
java -jar ysoSimple.jar -help YsoAttack
```

## c.详细使用

具体的使用方式见wiki文档

# 0x03 工具打包

Requires Java 1.7+ and Maven 3.x+

```bash
mvn clean package -DskipTests
```

# 0x04 感谢

[https://github.com/woodpecker-framework/ysoserial-for-woodpecker](https://github.com/woodpecker-framework/ysoserial-for-woodpecker)

[https://github.com/X1r0z/JNDIMap](https://github.com/X1r0z/JNDIMap)

[https://github.com/Java-Chains/web-chains](https://github.com/Java-Chains/web-chains)

[https://raw.githubusercontent.com/LandGrey/SpringBootVulExploit/master/codebase/JNDIObject.java](https://raw.githubusercontent.com/LandGrey/SpringBootVulExploit/master/codebase/JNDIObject.java)

[https://github.com/Whoopsunix/utf-8-overlong-encoding](https://github.com/Whoopsunix/utf-8-overlong-encoding)

[https://github.com/luelueking/Deserial_Sink_With_JDBC](https://github.com/luelueking/Deserial_Sink_With_JDBC)

[https://github.com/h0ny/NacosExploit](https://github.com/h0ny/NacosExploit)

[https://github.com/SummerSec/ShiroAttack2](https://github.com/SummerSec/ShiroAttack2)

‍
