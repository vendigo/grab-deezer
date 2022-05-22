package com.github.vendigo.grabdeezer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableNeo4jRepositories
public class GrabDeezerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GrabDeezerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
