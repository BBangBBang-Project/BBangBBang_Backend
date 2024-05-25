package test.bbang.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","http://localhost:8081")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 이미지 파일이 저장된 디렉토리를 나타냄
        String uploadsDir = "file:/home/ubuntu/bbang/uploads/";
        // "/images/**"로 들어오는 모든 요청을 uploads 디렉토리에서 찾도록 리소스 핸들러 설정
        registry.addResourceHandler("/images/**").addResourceLocations(uploadsDir);

    }
}
