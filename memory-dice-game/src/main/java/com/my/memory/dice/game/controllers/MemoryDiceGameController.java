/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game.controllers;

import com.my.memory.dice.game.dto.DiceContainer;
import com.my.memory.dice.game.services.DiceRollerService;
import com.my.memory.dice.game.utils.IntegerListTableCellFactory;
import com.my.memory.dice.game.utils.TimeConverter;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tbee.javafx.scene.layout.MigPane;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.scene.control.Alert.*;

@Component
public class MemoryDiceGameController implements Initializable
{
    private static final int MAX_CONTAINER_NUMBER = 100;
    private static final int MAX_DICE_NUMBER = 10;
    private static final int MAX_QUESTION = 5;

    @FXML
    private BorderPane rootPane;

    @FXML
    private MigPane questionPane;

    @FXML
    private TableView<DiceContainer> table;

    @FXML
    private TableColumn<DiceContainer, Integer> numberColumn;

    @FXML
    private TableColumn<DiceContainer, DiceContainer> diceColumn;

    @FXML
    private TextField numberOfContainer;

    @FXML
    private TextField numberOfDice;

    @FXML
    private Button btnHint;

    @FXML
    private Pagination pagination;

    @Autowired
    private DiceRollerService diceRoller;

    private ObservableList<DiceContainer> diceContainers = FXCollections.observableArrayList();

    //cache value
    private int containerCount;
    private int diceCount;
    private List<DiceContainer> containers;
    private List<Integer> list;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        numberColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getNumber()));
        diceColumn.setCellFactory(col -> new IntegerListTableCellFactory<>(DiceContainer::getDice));
        diceColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue()));

        numberOfContainer.textProperty().addListener((observable, oldValue, newValue) -> {
            applyNumeric(numberOfContainer, newValue);
            applyMaxLength(numberOfContainer, 3, newValue);
        });

        numberOfDice.textProperty().addListener((observable, oldValue, newValue) -> {
            applyNumeric(numberOfDice, newValue);
            applyMaxLength(numberOfDice, 2, newValue);
        });

        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(11)));
        table.setItems(diceContainers);

        btnHint.disableProperty().bind(Bindings.size(questionPane.getChildren()).greaterThan(0));
        btnHint.disableProperty().bind(Bindings.size(table.getItems()).greaterThan(0));
    }

    @FXML
    public void submit()
    {
        loadMemoryZone();
    }

    @FXML
    public void hint()
    {
        disableQuestionPane(true);
        diceContainers.setAll(containers);
        activateTimer(true, 5);
    }

    private int retrieveBufferTime(int containerCount, int diceCount)
    {
        if(containerCount > 5 && containerCount < 20) return diceCount >= 3 ? 60 : 30;
        if(containerCount >= 20 && containerCount <= 30) return diceCount >= 3 ? 90 : 45;
        else if(containerCount > 30 && containerCount <= 50) return diceCount >= 3 ? 120 : 60;
        else if(containerCount > 50) return diceCount >= 3 ? 240 : 120;
        else return diceCount >= 3 ? 10 : 5;
    }

    private void activateTimer(int bufferTime)
    {
        activateTimer(false, bufferTime);
    }

    private void activateTimer(boolean isHint, int bufferTime)
    {
        PauseTransition timer = new PauseTransition(Duration.seconds(bufferTime));
        timer.setOnFinished(event -> {
            diceContainers.setAll(FXCollections.observableArrayList());
            if(!isHint) loadQuestions(true);
            else disableQuestionPane(false);
        });
        timer.play();
    }

    private void loadMemoryZone()
    {
        containerCount = convertStringToPrimitiveInt(numberOfContainer.getText());
        diceCount = convertStringToPrimitiveInt(numberOfDice.getText());

        if(isValid(containerCount, diceCount))
        {
            containerCount = Math.min(containerCount, MAX_CONTAINER_NUMBER);
            diceCount = Math.min(diceCount, MAX_DICE_NUMBER);

            int bufferTime = retrieveBufferTime(containerCount, diceCount);
            String contextText = String.format("You got %s to memorize.", TimeConverter.bySeconds(bufferTime));
            prompt(AlertType.INFORMATION, "Game Rule", contextText);

            diceRoller.roll(containerCount, diceCount);

            containers = diceRoller.getContainers().entrySet()
                    .stream()
                    .map(entry -> new DiceContainer(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            diceContainers.setAll(FXCollections.observableArrayList(containers));
            activateTimer(bufferTime);
            loadQuestions(false);
            reset();
        }
    }

    private int convertStringToPrimitiveInt(String text)
    {
        if(text.isEmpty()) return 0;
        return Integer.parseInt(text);
    }

    private boolean isValid(int containerCount, int diceCount)
    {
        String message = null;
        if(containerCount == 0 && diceCount == 0)
        {
            message = "Failed to start game due 0 container and no dice";
        }
        else if(containerCount == 0)
        {
            message = "Failed to start game due to 0 container.";
        }
        else if(diceCount == 0)
        {
            message = "Failed to start game due to no dice in container.";
        }

        if(Optional.ofNullable(message).isPresent())
        {
            prompt(AlertType.ERROR, "Reminder", message);
            return false;
        }

        return true;
    }

    private void loadQuestions(boolean isVisible)
    {
        questionPane.getChildren().clear();
        questionPane.setVisible(isVisible);
        if(isVisible) createQuestions();
    }

    private List<Integer> generateContainerNumbers()
    {
        List<Integer> questions = new ArrayList<>();

        IntStream.range(0, Math.min(containerCount, MAX_QUESTION)).forEach(i -> {
            int containerNumber = diceRoller.randomizer(0, containerCount);
            while(questions.contains(containerNumber))
            {
                containerNumber = diceRoller.randomizer(0, containerCount);
            }
            questions.add(containerNumber);
        });

        return questions;
    }

    private void createQuestions()
    {
        generateContainerNumbers().forEach(number -> {
            String question = String.format("List dice numbers in sequence for container %s", number);
            Label lblQuestion = new Label(question);
            TextField answer = new TextField();
            Button submit = new Button("Submit");
            answer.textProperty().addListener((observable, oldValue, newValue) -> {
                applyNumeric(answer, newValue);
                applyMaxLength(answer, newValue);
            });

            submit.setOnAction(event -> {
                boolean isCorrect = diceRoller.check(number, convertStringToList(answer.getText()));
                promptDialog(isCorrect);
                answer.disableProperty().bind(Bindings.createBooleanBinding(() -> isCorrect));
                submit.disableProperty().bind(Bindings.createBooleanBinding(() -> isCorrect));

                List<Node> nodes = questionPane.getChildren();
                list = IntStream.range(0, nodes.size()).boxed()
                        .filter(i -> !(nodes.get(i) instanceof Label))
                        .filter(i -> nodes.get(i).isDisable())
                        .collect(Collectors.toList());

                if(list.size() == 10)
                {
                    prompt(AlertType.INFORMATION, "Congratulation", "You answered all correctly!");
                }
            });

            questionPane.add(lblQuestion);
            questionPane.add(answer);
            questionPane.add(submit);
        });
    }

    private void disableQuestionPane(boolean isDisable)
    {
        List<Node> nodes = questionPane.getChildren();
        IntStream.range(0, nodes.size()).boxed()
                .filter(i -> !(nodes.get(i) instanceof Label))
                .filter(i -> !Optional.ofNullable(list).orElse(new ArrayList<>()).contains(i))
                .forEach(i -> {
                    nodes.get(i).disableProperty().unbind();
                    nodes.get(i).disableProperty().set(isDisable);
                });
    }

    private void promptDialog(boolean isCorrect)
    {
        if(isCorrect) prompt(AlertType.INFORMATION, "Congratulation", "Bingo!");
        else prompt(AlertType.ERROR, "Wrong Guess", "Try Again!");
    }

    private void prompt(AlertType type, String title, String contentText)
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void applyNumeric(TextField control, String newValue)
    {
        if (!newValue.matches("\\d*"))
        {
            control.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }

    private void applyMaxLength(TextField control, String newValue)
    {
        applyMaxLength(control, diceCount, newValue);
    }

    private void applyMaxLength(TextField control, int maxLength, String newValue)
    {
        if (newValue.length() > maxLength)
        {
            control.setText(newValue.substring(0, maxLength));
        }
    }

    private List<Integer> convertStringToList(String toBeConverted)
    {
        return IntStream.range(0, toBeConverted.length())
                .mapToObj(i -> Integer.parseInt(toBeConverted.substring(i, i + 1)))
                .collect(Collectors.toList());
    }

    private void reset()
    {
        numberOfContainer.clear();
        numberOfDice.clear();
    }
}
