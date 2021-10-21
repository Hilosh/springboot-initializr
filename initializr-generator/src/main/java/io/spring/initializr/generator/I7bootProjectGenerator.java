package io.spring.initializr.generator;

import freemarker.template.Configuration;
import io.spring.initializr.i7boot.ModuleDecorateChain;
import io.spring.initializr.i7boot.ModuleDecorator;
import io.spring.initializr.i7boot.ProjectConstructGenerateContext;
import io.spring.initializr.i7boot.base.BaseModuleDecorator;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author jyuh
 * @date 2021-10-20 13:57
 */
public class I7bootProjectGenerator extends ProjectGenerator {

    private final PriorityQueue<ModuleDecorator> decorators = new PriorityQueue<>(Comparator.comparing(ModuleDecorator::getOrder));

    @Override
    protected File generateProjectStructure(ProjectRequest request, Map<String, Object> model) {
        File rootDir;
        try {
            rootDir = File.createTempFile("tmp", "", getTemporaryDirectory());
        }
        catch (IOException ex) {
            throw new IllegalStateException("Cannot create temp dir", ex);
        }
        addTempFile(rootDir.getName(), rootDir);
        rootDir.delete();
        rootDir.mkdirs();
        File dir = initializerProjectDir(rootDir, request);
        decorators.add(new BaseModuleDecorator());
        decorateProject(ProjectConstructGenerateContext.wrapperProjectRequest(request, model, dir));
        return rootDir;
    }

    private void decorateProject(ProjectConstructGenerateContext context) {
        for (ModuleDecorator decorator : decorators) {
            decorator.decorate(context);
        }
    }

}
