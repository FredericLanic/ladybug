package com.kycox.game;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");

    public static void main(String[] args) {
        MainLadybug mainLadybug = (MainLadybug) applicationContext.getBean("mainLadybug");
        mainLadybug.launchTheGame();
    }

}
