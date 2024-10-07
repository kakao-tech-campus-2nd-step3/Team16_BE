package org.cookieandkakao.babting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableCaching
public class BabtingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BabtingApplication.class, args);
    }

}
