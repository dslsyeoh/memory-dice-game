/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game;

import com.my.memory.dice.game.services.DiceRollerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDiceRollerServiceHandler
{
    @Autowired
    private DiceRollerService service;

    @Test
    public void test_roll_10_containers_4_dice_each()
    {
        service.roll(10, 4);
        assertEquals(10, service.getContainers().size());
        service.getContainers().forEach((k, dice) -> assertEquals(4, dice.size()));
    }

    @Test
    public void test_randomizer_100_times()
    {
        IntStream.range(0, 100).forEach(i -> assertTrue(service.randomizer(1, 7) < 7));
    }

    @Test
    public void test_check_answer_is_equal()
    {
        service.roll(10, 4);
        //stimulate user input correct
        assertTrue(service.check(1, service.getContainers().get(1)));
    }

    @Test
    public void test_check_answer_is_not_equal()
    {
        service.roll(10, 4);
        //stimulate user input wrong
        assertFalse(service.check(1, service.getContainers().get(2)));
    }

    private Map<Integer, List<Integer>> containers = new HashMap<Integer, List<Integer>>()
    {
        {
            put(1, Arrays.asList(1, 2, 5, 6));
            put(2, Arrays.asList(1, 4, 2, 3));
            put(3, Arrays.asList(2, 2, 5, 6));
            put(4, Arrays.asList(1, 1, 1, 1));
            put(5, Arrays.asList(1, 4, 5, 3));
            put(6, Arrays.asList(1, 2, 6, 6));
            put(7, Arrays.asList(4, 3, 1, 6));
            put(8, Arrays.asList(5, 2, 1, 6));
            put(9, Arrays.asList(1, 4, 4, 6));
            put(10, Arrays.asList(1, 2, 3, 3));

        }
    };
}
