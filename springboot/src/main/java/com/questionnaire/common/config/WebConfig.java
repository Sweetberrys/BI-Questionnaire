package com.questionnaire.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
//
//@Configuration
//public class WebConfig extends WebMvcConfigurationSupport{
//
//    @Resource
//    private JwtInterceptor jwtInterceptor;
//
//    // 加自定义拦截器JwtInterceptor，设置拦截规则
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
//                .excludePathPatterns("/")
//                .excludePathPatterns("/login")
//                .excludePathPatterns("/register")
//                .excludePathPatterns("/files/**")
//                .excludePathPatterns("/fileOperation/**")
//                .excludePathPatterns("/pages/selectById/**")
//                .excludePathPatterns("/question/selectByPageId/**")
//                .excludePathPatterns("/answer/addBatch")
//                .excludePathPatterns("/pages/selectAll")
//                .excludePathPatterns("/notice/selectAll");
//    }
//
//}

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Resource
    private JwtInterceptor jwtInterceptor;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/v2/api-docs",           // 放行Swagger的API文档获取路径
                        "/swagger-resources/**",  // 放行Swagger的资源文件路径
                        "/webjars/**",            // 放行Swagger的WebJars路径
                        "/swagger-ui.html",       // 放行Swagger UI页面
                        //"/login",
                        "/register",
                        "/files/**",
                        "/fileOperation/**",
                        "/pages/selectById/**",
                        "/question/selectByPageId/**",
                        "/answer/addBatch",
                        "/pages/selectAll",
                        "/notice/selectAll"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
    }
}
