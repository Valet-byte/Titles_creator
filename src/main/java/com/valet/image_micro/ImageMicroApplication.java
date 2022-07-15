package com.valet.image_micro;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ImageMicroApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ImageMicroApplication.class);
        builder.headless(false).run(args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void test(){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

}

