package com.sparkinex.lpautopromote.core;

import net.minecraft.world.phys.Vec3;

public class PlayerData {

    private double prevX;
    private double prevY;
    private double prevZ;

    private double currX;
    private double currY;
    private double currZ;

    private Vec3 currLookAngle;
    private Vec3 prevLookAngle;

    public PlayerData(double x, double y, double z, Vec3 lookAngle) {
        this.currX = x;
        this.currY = y;
        this.currZ = z;

        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;

        this.currLookAngle = lookAngle;
        this.prevLookAngle = lookAngle;
    }

    public void update(double x, double y, double z, Vec3 lookAngle) {
        this.prevX = this.currX;
        this.prevY = this.currY;
        this.prevZ = this.currZ;

        this.prevLookAngle = this.currLookAngle;

        this.currX = x;
        this.currY = y;
        this.currZ = z;

        this.currLookAngle = lookAngle;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getPrevZ() {
        return prevZ;
    }

    public double getCurrX() {
        return currX;
    }

    public double getCurrY() {
        return currY;
    }

    public double getCurrZ() {
        return currZ;
    }

    public Vec3 getCurrLookAngle() {
        return currLookAngle;
    }

    public Vec3 getPrevLookAngle() {
        return prevLookAngle;
    }
}
