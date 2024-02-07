package com.whale.web.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class RandomUtils {

    @Bean
    public Random random(){
        return new Random();
    }
}
