package com.kycox.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class Application {
    private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");

    public static void main(String[] args) {
        MainLadybug mainLadybug = applicationContext.getBean("mainLadybug", MainLadybug.class);
        mainLadybug.launchTheGame();
    }

//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
//
//    public void run(String... args) {
//        MainLadybug mainLadybug = applicationContext.getBean("mainLadybug", MainLadybug.class);
//        mainLadybug.launchTheGame();
//    }
}
