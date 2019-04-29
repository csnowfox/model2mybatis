package org.csnowfox.maven.plugin.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName: Application
 * @Description TODO
 * @Author Csnowfox
 * @Date 2019/4/27 19:02
 **/

@SpringBootApplication
@MapperScan("org.csnowfox.maven.plugin.example.dao.fund")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
