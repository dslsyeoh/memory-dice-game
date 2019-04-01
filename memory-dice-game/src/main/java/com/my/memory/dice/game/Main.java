/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application
{
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception
    {
        applicationContext = SpringApplication.run(Main.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = ContextProvider.load(getClass().getResource("/com.my.memory.dice.game/memory_dice_game.fxml"));

        stage.setScene(new Scene(root, 840, 640));
        stage.setTitle("Memory Dice Game");
        stage.show();
    }

    @Override
    public void stop() throws Exception
    {
        applicationContext.close();
    }
}
