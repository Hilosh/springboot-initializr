package io.spring.initializr.generator;

import io.spring.initializr.i7boot.Module;
import io.spring.initializr.i7boot.ModuleDecorator;
import io.spring.initializr.i7boot.ProjectConstructGenerateContext;
import io.spring.initializr.i7boot.base.BaseModuleDecorator;
import io.spring.initializr.metadata.Dependency;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jyuh
 * @date 2021-10-20 13:57
 */
public class I7bootProjectGenerator extends ProjectGenerator {

    private final PriorityQueue<ModuleDecorator> decorators = new PriorityQueue<>(Comparator.comparing(ModuleDecorator::getOrder));

    public I7bootProjectGenerator() {
        super();
        for (ModuleDecorator moduleDecorator : ServiceLoader.load(ModuleDecorator.class)) {
            Module module = moduleDecorator.getClass().getAnnotation(Module.class);
            if (module == null || StringUtils.isBlank(module.suffix())) {
                throw new IllegalArgumentException(moduleDecorator.getClass().getName() + " has not defined module suffix with annotation @Module.");
            }
            decorators.add(moduleDecorator);
        }
    }

    @Override
    protected File generateProjectStructure(ProjectRequest request, Map<String, Object> model) {
        File rootDir;
        try {
            rootDir = File.createTempFile("tmp", "", getTemporaryDirectory());
        } catch (IOException ex) {
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
        Map<String, Map<String, List<String>>> resolvedDeps = new HashMap<>();
        for (Dependency dependency : context.getResolvedDependencies()) {
            Map<String, List<String>> jarsInSameGroup = resolvedDeps.computeIfAbsent(dependency.getGroupId(), k -> new HashMap<>());
            List<String> jarsInSameArtifact = jarsInSameGroup.computeIfAbsent(dependency.getArtifactId(), k -> new ArrayList<>());
            jarsInSameArtifact.add(dependency.getVersion());
        }
        for (ModuleDecorator decorator : decorators) {
            Module module = decorator.getClass().getAnnotation(Module.class);
            if (includeDep(resolvedDeps, module)) {
                decorator.decorate(context);
            }
        }
    }

    private boolean includeDep(Map<String, Map<String, List<String>>> dependenciesTree, Module module) {
        if (StringUtils.equals(module.dependencyGroupId(), "*")
                && StringUtils.equals(module.dependencyArtifactId(), "*")
                && StringUtils.equals(module.version(), "*")) {
            return true;
        }
        List<Map<String, List<String>>> values = new ArrayList<>();
        if (StringUtils.equals(module.dependencyGroupId(), "*")) {
            values.addAll(dependenciesTree.values());
        } else {
            values.add(dependenciesTree.get(module.dependencyGroupId()));
        }
        Set<String> versions = new HashSet<>();
        for (Map<String, List<String>> value : values) {
            if (StringUtils.equals(module.dependencyArtifactId(), "*")) {
                versions.addAll(value.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
            } else {
                versions.addAll(value.get(module.dependencyGroupId()));
            }
        }
        return StringUtils.equals(module.version(), "*") ? versions.size() > 0 : versions.contains(module.version());
    }

}
