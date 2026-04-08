package com.spinmaster;

public class Reward {

    public String command;
    public double chance;
    public String rarity;

    public Reward(String command, double chance, String rarity) {
        this.command = command;
        this.chance = chance;
        this.rarity = rarity;
    }
}