package com.github.vendigo.grabdeezer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GrabDeezerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GrabDeezerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
