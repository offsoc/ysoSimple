package cn.butler.jndi.controller;

/**
 * 每一种Controller中的process方法和带有@JNDIMapping注解的路由有一定区别
 *  - @JNDIMapping方法：先调用,每条打法的Effect效果
 *  - process方法：后调用,返回Byte[]/Reference/CodeBase
 */
public interface Controller {
}
