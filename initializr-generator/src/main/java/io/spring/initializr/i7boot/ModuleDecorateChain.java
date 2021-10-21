package io.spring.initializr.i7boot;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author jyuh
 * @date 2021-10-20 14:39
 */
public class ModuleDecorateChain {

    private final PriorityQueue<ModuleDecorator> decorators = new PriorityQueue<>(Comparator.comparing(ModuleDecorator::getOrder));

    private final ProjectConstructGenerateContext context;

    public ModuleDecorateChain(ProjectConstructGenerateContext context, ModuleDecorator first) {
        this.context = context;
        decorators.add(first);
    }

    public void process() {
        for (ModuleDecorator decorator : decorators) {
            decorator.decorate(context);
        }
    }
}
