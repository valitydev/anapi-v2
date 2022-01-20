package dev.vality.anapi.v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class AnapiV2Application extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnapiV2Application.class, args);
    }

}
