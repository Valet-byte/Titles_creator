package com.valet.image_micro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Titer {
    private List<String> text;
    private String font;
    private String color;
    private String backgroundColor;
    private boolean centring;
    private double offset;
    private float textSize;
    private int fontType;
    private int speed;
    private int distance;
}
