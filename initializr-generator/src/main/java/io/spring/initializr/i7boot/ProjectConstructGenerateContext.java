package io.spring.initializr.i7boot;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.spring.initializr.generator.ProjectRequest;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jyuh
 * @date 2021-10-20 14:50
 */
public class ProjectConstructGenerateContext extends ProjectRequest {

    public static final String PARENT_DEPENDENCIES_COL_KEY = "parentPomDependencies";

    private static final String TEMPLATE_PARENT_DIR = "src/main/resources/templates/i7boot";

    /**
     * 项目根路径
     */
    private String rootDir;

    /**
     * 项目生成路径
     */
    private File rootFile;

    /**
     * 模版驱动类
     */
    private Configuration configuration;

    /**
     * 存放用于模版生成的自定义参数
     */
    private final Map<String, Object> templateData = new HashMap<>();

    public static ProjectConstructGenerateContext wrapperProjectRequest(ProjectRequest request, Map<String, Object> model, File rootFile) {
        ProjectConstructGenerateContext context = new ProjectConstructGenerateContext();
        context.setRootDir(rootFile.getAbsolutePath());
        context.setRootFile(rootFile);
        context.setResolvedDependencies(new ArrayList<>(request.getResolvedDependencies()));
        context.setFacets(new ArrayList<>(request.getFacets()));
        context.setBuild(request.getBuild());
        context.setStyle(new ArrayList<>(request.getStyle()));
        context.setDependencies(new ArrayList<>(request.getDependencies()));
        context.setName(request.getName());
        context.setType(request.getType());
        context.setDescription(request.getDescription());
        context.setGroupId(request.getGroupId());
        context.setArtifactId(request.getArtifactId());
        context.setVersion(request.getVersion());
        context.setBootVersion(request.getBootVersion());
        context.setPackaging(request.getPackaging());
        context.setApplicationName(request.getApplicationName());
        context.setLanguage(request.getLanguage());
        context.setPackageName(request.getPackageName());
        context.setJavaVersion(request.getJavaVersion());
        context.setBaseDir(request.getBaseDir());
        context.setConfiguration(new Configuration());
        context.getTemplateData().putAll(model);
        return context;
    }

    /**
     *
     * @param moduleTemplateDir 子文件夹路径(under template parent dir path)
     * @param generateInfos <模版文件， 输出文件路径(under project root dir path)>
     */
    public void generateWithTemplate(String moduleTemplateDir, Map<String, String> generateInfos) {
        try {
            ClassPathResource cpr = new ClassPathResource(moduleTemplateDir);
            configuration.setDirectoryForTemplateLoading(cpr.getFile());
            BufferedWriter out = null;
            for (Map.Entry<String, String> generateInfo : generateInfos.entrySet()) {
                try {
                    Template template = configuration.getTemplate(generateInfo.getKey());
                    File targetOutputFile = Paths.get(rootDir, generateInfo.getValue()).toFile();
                    if (!targetOutputFile.exists()) {
                        targetOutputFile.mkdirs();
                    }
                    targetOutputFile.delete();
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetOutputFile)));
                    template.process(templateData, out);
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("set freeMarker configuration directory error");
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public File getRootFile() {
        return rootFile;
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
    }

    public boolean addTemplateData(String key, Object o) {
        Object absent = templateData.putIfAbsent(key, o);
        if (absent == null) {
            return true;
        }
        return false;
    }

    public boolean removeTemplateData(String key, Object o) {
        return templateData.remove(key, o);
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }
}
