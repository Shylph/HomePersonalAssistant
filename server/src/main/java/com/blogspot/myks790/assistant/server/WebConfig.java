package com.blogspot.myks790.assistant.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/setting");
        registry.addViewController("/home_info");
        registry.addViewController("/week_report");
        registry.addViewController("/register_equipment");
    }
}
