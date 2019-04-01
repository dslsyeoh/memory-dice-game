/*
 * Written by Steven Yeoh
 *
 * Copyright (c) 2019.
 */

package com.my.memory.dice.game.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import java.util.List;

public class IntegerListTableCellFactory<S, T> extends TableCell<S, T>
{
    public IntegerListTableCellFactory(Callback<T, List<Integer>> value)
    {
        setValue(value);
    }

    @Override
    protected void updateItem(T item, boolean empty)
    {
       super.updateItem(item, empty);
       if(empty || item == null)
       {
           setText("");
           setGraphic(null);
       }
       else
       {
           if(getValue() == null)
           {
                setText(item.toString());
           }
           else
           {
               setText(format(getValue().call(item)));
           }
       }
    }

    private final ObjectProperty<Callback<T, List<Integer>>> valueProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Callback<T, List<Integer>>> valueProperty()
    {
        return valueProperty;
    }

    private void setValue(Callback<T, List<Integer>> value)
    {
        valueProperty().set(value);
    }

    private Callback<T, List<Integer>> getValue()
    {
        return valueProperty().get();
    }

    private String format(List<Integer> dice)
    {
        return dice.toString().replace("[", "").replace("]", "").replace(",", " ");
    }
}
