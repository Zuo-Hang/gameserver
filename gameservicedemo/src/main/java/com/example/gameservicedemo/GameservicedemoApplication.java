package com.example.gameservicedemo;

import com.example.gameservicedemo.server.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.gameservicedemo", "com.example.gamedatademo"})
@EnableScheduling
public class GameservicedemoApplication implements CommandLineRunner {
    @Autowired
    private TcpServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(GameservicedemoApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();

    }
}
