/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game.services;

import java.util.List;
import java.util.Map;

public interface DiceRollerService
{
    void roll(int numberOfContainer, int numberOfDice);

    int randomizer(int min, int max);

    boolean check(int number, List<Integer> actual);

    Map<Integer, List<Integer>> getContainers();
}
