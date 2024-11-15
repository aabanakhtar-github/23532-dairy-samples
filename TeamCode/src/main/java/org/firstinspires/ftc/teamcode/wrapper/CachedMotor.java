package org.firstinspires.ftc.teamcode.wrapper;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/*
    Cached Motor for optimal performance
 */
public class CachedMotor implements DcMotorSimple {
    public DcMotorEx __IMPL;
    private double cache = 0.0f;

    public CachedMotor(HardwareMap map, String name) {
        __IMPL = map.get(DcMotorEx.class, name);
    }

    @Override
    public void setDirection(Direction direction) {
        __IMPL.setDirection(direction);
    }

    public void setMode(DcMotor.RunMode mode) {
        __IMPL.setMode(mode);
    }

    @Override
    public Direction getDirection() {
        return __IMPL.getDirection();
    }

    @Override
    public void setPower(double power) {
        if (power != cache) {
            cache = power;
            __IMPL.setPower(power);
        }
    }

    @Override
    public double getPower() {
        return cache;
    }

    @Override
    public Manufacturer getManufacturer() {
        return __IMPL.getManufacturer();
    }

    @Override
    public String getDeviceName() {
        return  __IMPL.getDeviceName();
    }

    @Override
    public String getConnectionInfo() {
        return __IMPL.getConnectionInfo();
    }

    @Override
    public int getVersion() {
        return __IMPL.getVersion();
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        __IMPL.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void close() {
        __IMPL.close();
    }
}
