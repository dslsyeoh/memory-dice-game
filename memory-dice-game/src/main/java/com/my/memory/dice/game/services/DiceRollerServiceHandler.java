/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DiceRollerServiceHandler implements DiceRollerService
{
    private Map<Integer, List<Integer>> containers;

    @Override
    public void roll(int numberOfContainer, int numberOfDice)
    {
        containers = IntStream.rangeClosed(1, numberOfContainer).boxed().collect(Collectors.toMap(number -> number, number -> rollDice(numberOfDice)));
    }

    public List<Integer> rollDice(int size)
    {
        return IntStream.range(0, size).mapToObj(i-> randomizer(0, 6)).collect(Collectors.toList());
    }

    @Override
    public int randomizer(int min, int max)
    {
        Random generator = new Random();
        return generator.nextInt((max - min)) + 1;
    }

    @Override
    public boolean check(int number, List<Integer> actual)
    {
       return Objects.equals(containers.get(number), actual);
    }

    @Override
    public Map<Integer, List<Integer>> getContainers()
    {
        return containers;
    }
}
