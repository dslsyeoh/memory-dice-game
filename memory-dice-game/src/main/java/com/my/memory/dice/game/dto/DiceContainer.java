/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiceContainer
{
    private int number;
    private List<Integer> dice;
}
