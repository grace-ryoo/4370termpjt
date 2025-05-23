package termProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import termProject.components.AuthInterceptor;

/**
 * This is a configuration class. See comments in AuthInterceptor.java regarding
 * dependency injection and inversion of control.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // This is an object that allows redirection if user is logged in.
    private final AuthInterceptor authInterceptor;

    /**
     * An AuthInterceptor will be initialized and provided when a WebConfig is
     * initalized by Spring Boot.
     */
    @Autowired
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * This is where we register the interceptor to the URL patterns we want the
     * redirection in.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/login/**", "/register", "/css/**", "/js/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
