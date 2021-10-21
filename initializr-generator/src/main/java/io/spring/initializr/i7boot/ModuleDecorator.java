package io.spring.initializr.i7boot;

import org.springframework.core.Ordered;

/**
 * @author jyuh
 * @date 2021-10-19 18:48
 */
public interface ModuleDecorator extends Ordered {

    /**
     * 模块装饰器
     * @param context
     */
    void decorate(ProjectConstructGenerateContext context);
}
