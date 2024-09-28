package org.cookieandkakao.babting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class BabtingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BabtingApplication.class, args);
    }

}
