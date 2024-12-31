package cn.butler.yso;

public class JavassistClassLoader extends ClassLoader {
    public JavassistClassLoader(){
        super(Thread.currentThread().getContextClassLoader());
    }
}
