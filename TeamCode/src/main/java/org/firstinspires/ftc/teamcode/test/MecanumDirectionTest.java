package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.feature.subsystem.MecanumDrive;
import org.firstinspires.ftc.teamcode.wrapper.RoboticOpMode;

@TeleOp(name = "TEST\uD83E\uDDEA: Mecanum Drive Direction Test")
public class MecanumDirectionTest extends RoboticOpMode {
    @Override
    public void init() {
        MecanumDrive.INSTANCE.setDefaultCommand(MecanumDrive.INSTANCE.robotCentricDriveCommand());
    }

    @Override
    public void loop() {

    }
}