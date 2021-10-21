package io.spring.initializr.i7boot;

/**
 * @author jyuh
 * @date 2021-10-19 18:48
 */
public interface ModuleDecorator {

    /**
     * 执行顺序
     *
     * @return
     */
    int getOrder();

    /**
     * 模块装饰器
     * @param context
     */
    void decorate(ProjectConstructGenerateContext context);
}
