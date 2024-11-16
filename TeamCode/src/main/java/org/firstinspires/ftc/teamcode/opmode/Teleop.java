package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.feature.subsystem.MecanumDrive;
import org.firstinspires.ftc.teamcode.wrapper.RoboticOpMode;

import dev.frozenmilk.mercurial.Mercurial;

@TeleOp(name = "Driver Control")
public class Teleop extends RoboticOpMode {

    @Override
    public void init() {
        MecanumDrive.INSTANCE.setDefaultCommand(MecanumDrive.INSTANCE.robotCentricDriveCommand());
        Mercurial.gamepad1().a().onTrue();
        Mercurial.gamepad1().b().onTrue();
        Mercurial.gamepad1().dpadUp().onTrue();
    }

    @Override
    public void loop() {

    }
}
