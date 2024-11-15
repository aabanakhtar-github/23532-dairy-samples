package org.firstinspires.ftc.teamcode.wrapper;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.feature.subsystem.Hubs;
import org.firstinspires.ftc.teamcode.feature.subsystem.MecanumDrive;
import org.firstinspires.ftc.teamcode.feature.subsystem.ThreeDeadWheel;

import dev.frozenmilk.mercurial.Mercurial;

@Mercurial.Attach
@MecanumDrive.Attach
@Hubs.Attach
@ThreeDeadWheel.Attach
public abstract class RoboticOpMode extends OpMode {
}
