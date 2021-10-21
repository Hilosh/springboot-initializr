package io.spring.initializr.i7boot.base;

import io.spring.initializr.i7boot.Module;
import io.spring.initializr.i7boot.ModuleDecorator;
import io.spring.initializr.i7boot.ProjectConstructGenerateContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jyuh
 * @date 2021-10-20 14:52
 */
@Module(suffix = "main")
public class BaseModuleDecorator implements ModuleDecorator {
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void decorate(ProjectConstructGenerateContext context) {
        context.addTemplateData("packageName", context.getPackageName());
        context.addTemplateData("groupId", context.getGroupId());
        Map<String, String> templatesAndPath = new HashMap<>();
        templatesAndPath.put("main.ftl", "src/main/java/" + StringUtils.replace(context.getGroupId(), ".", "/") + "/" + context.getArtifactId() + "/" + context.getName().toLowerCase() + "-main/AppSampleBootApplication.java");
        context.generateWithTemplate("templates/i7boot/base", templatesAndPath);
    }
}
