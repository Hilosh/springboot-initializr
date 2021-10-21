package ${packageName}.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication()
@ImportResource(locations = {
    "classpath:/spring/app-deploy/spring-load.xml",
    "classpath:/spring/customize/*.xml"
})
@ComponentScan({"${packageName}"})
@MapperScan("${packageName}.dao")
public class AppSampleBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AppSampleBootApplication.class).run(args);
    }

}