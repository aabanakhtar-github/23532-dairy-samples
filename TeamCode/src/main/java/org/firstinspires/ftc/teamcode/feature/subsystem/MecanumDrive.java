package org.firstinspires.ftc.teamcode.feature.subsystem;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.ftccommon.internal.manualcontrol.parameters.ImuParameters;
import org.firstinspires.ftc.teamcode.control.FeedforwardTerm;
import org.firstinspires.ftc.teamcode.wrapper.CachedMotor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.frozenmilk.dairy.core.FeatureRegistrar;
import dev.frozenmilk.dairy.core.dependency.Dependency;
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation;
import dev.frozenmilk.dairy.core.util.controller.calculation.pid.DoubleComponent;
import dev.frozenmilk.dairy.core.util.controller.implementation.DoubleController;
import dev.frozenmilk.dairy.core.util.controller.implementation.DoublePoseController;
import dev.frozenmilk.dairy.core.util.supplier.numeric.CachedMotionComponentSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.EnhancedDoubleSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.MotionComponents;
import dev.frozenmilk.dairy.core.wrapper.Wrapper;
import dev.frozenmilk.mercurial.Mercurial;
import dev.frozenmilk.mercurial.bindings.BoundGamepad;
import dev.frozenmilk.mercurial.commands.Lambda;
import dev.frozenmilk.mercurial.subsystems.Subsystem;
import dev.frozenmilk.mercurial.subsystems.SubsystemObjectCell;
import kotlin.annotation.MustBeDocumented;

@Config
public class MecanumDrive implements Subsystem {
    // Add mercurial as a dependency
    private Dependency<?> dependencies = Subsystem.DEFAULT_DEPENDENCY
            .and(new SingleAnnotation<>(Attach.class));
    public static final MecanumDrive INSTANCE = new MecanumDrive();

    private final SubsystemObjectCell<CachedMotor> leftFront = subsystemCell(() ->
            new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "leftFront"));
    private final SubsystemObjectCell<CachedMotor> rightFront = subsystemCell(() ->
            new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "rightFront"));
    private final SubsystemObjectCell<CachedMotor> rightBack = subsystemCell(() ->
            new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "rightBack"));
    private final SubsystemObjectCell<CachedMotor> leftBack = subsystemCell(() ->
            new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "leftBack"));

    private MecanumDrive() {

    }

    @NonNull
    @Override
    public Dependency<?> getDependency() {
        return dependencies;
    }

    @Override
    public void setDependency(@NonNull Dependency<?> dependency) {
        this.dependencies = dependency;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(java.lang.annotation.ElementType.TYPE)
    @MustBeDocumented
    @Inherited
    public @interface Attach {
    }

    @Override
    public void preUserInitHook(@NonNull Wrapper opMode) {
        getLeftFront().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        getRightFront().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        getLeftBack().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        getRightBack().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        getRightBack().setDirection(DcMotorSimple.Direction.REVERSE);
        getRightFront().setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void cleanup(@NonNull Wrapper opmode) {
        leftFront.invalidate();
        rightFront.invalidate();
        leftBack.invalidate();
        rightBack.invalidate();
    }

    /****** GETTERS */
    public CachedMotor getLeftFront() { return leftFront.get(); }
    public CachedMotor getRightFront() { return rightFront.get(); }
    public CachedMotor getLeftBack() { return leftBack.get(); }
    public CachedMotor getRightBack() { return rightBack.get(); }

    /******** MERCURIAL HOOKS */
    @Override
    public void preUserInitLoopHook(@NonNull Wrapper opmode) {
    }

    /******* COMMANDS */
    // drives the robot
    public Lambda robotCentricDriveCommand() {
        BoundGamepad gamepad1 = Mercurial.gamepad1();
        return new Lambda("mecanum-drive-robot-centric")
                .setInit(() -> {

                })
                .setExecute(() -> {
                    double x = 0.5 * -gamepad1.leftStickX().state();
                    double y = 0.5 * gamepad1.leftStickY().state();
                    double rx = 0.5 * -gamepad1.rightStickX().state();
                    double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
                    double frontLeftPower = (y + x + rx) / denominator;
                    double backLeftPower = (y - x + rx) / denominator;
                    double frontRightPower = (y - x - rx) / denominator;
                    double backRightPower = (y + x - rx) / denominator;

                    getLeftFront().setPower(frontLeftPower);
                    getLeftBack().setPower(backLeftPower);
                    getRightFront().setPower(frontRightPower);
                    getRightBack().setPower(backRightPower);
                })
                .setFinish(() -> false);
    }
}