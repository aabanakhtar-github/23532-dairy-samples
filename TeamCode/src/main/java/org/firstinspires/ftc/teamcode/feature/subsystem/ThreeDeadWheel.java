package org.firstinspires.ftc.teamcode.feature.subsystem;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import dev.frozenmilk.dairy.core.FeatureRegistrar;
import dev.frozenmilk.dairy.core.dependency.Dependency;
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation;
import dev.frozenmilk.dairy.core.wrapper.Wrapper;
import dev.frozenmilk.mercurial.subsystems.Subsystem;
import dev.frozenmilk.mercurial.subsystems.SubsystemObjectCell;
import kotlin.annotation.MustBeDocumented;

@Config
public class ThreeDeadWheel implements Subsystem {
    private Dependency<?> dependencies = DEFAULT_DEPENDENCY
            .and(new SingleAnnotation<>(Attach.class));
    public static ThreeDeadWheel INSTANCE = new ThreeDeadWheel();

    private SubsystemObjectCell<DcMotor> perp = subsystemCell(() ->
            FeatureRegistrar.getActiveOpMode().hardwareMap.get(DcMotor.class, "rightBack"));
    private SubsystemObjectCell<DcMotor> left = subsystemCell(() ->
            FeatureRegistrar.getActiveOpMode().hardwareMap.get(DcMotor.class, "leftBack"));
    private SubsystemObjectCell<DcMotor> right = subsystemCell(() ->
            FeatureRegistrar.getActiveOpMode().hardwareMap.get(DcMotor.class, "rightFront"));

    // inches
    public static double trackWidth = 15.848543;
    public static double centerWheelOffset = 8.87;
    public static double inPerTick = (Math.PI * 1.25984252) / 2000.0;

    SubsystemObjectCell<HolonomicOdometry> odometry = subsystemCell(() -> new HolonomicOdometry(
            () -> left.get().getCurrentPosition() * inPerTick,
            () -> right.get().getCurrentPosition() * inPerTick,
            () -> perp.get().getCurrentPosition() * inPerTick,
            trackWidth, centerWheelOffset)
    );

    private ThreeDeadWheel() {

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
        left.get().setDirection(DcMotorSimple.Direction.REVERSE);
        left.get().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.get().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        perp.get().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left.get().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.get().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        perp.get().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        odometry.get().updatePose(new Pose2d(0, 0, new Rotation2d(0.0)));
    }

    @Override
    public void preUserLoopHook(@NonNull Wrapper opMode) {
        odometry.get().updatePose();
        FeatureRegistrar.getActiveOpMode().telemetry.addData("tick l", left.get().getCurrentPosition());
        FeatureRegistrar.getActiveOpMode().telemetry.addData("tick r",  right.get().getCurrentPosition());
        FeatureRegistrar.getActiveOpMode().telemetry.addData("tick p", perp.get().getCurrentPosition());
        FeatureRegistrar.getActiveOpMode().telemetry.addData("Pose X", odometry.get().getPose().getX());
        FeatureRegistrar.getActiveOpMode().telemetry.addData("Pose Y", odometry.get().getPose().getY());
        FeatureRegistrar.getActiveOpMode().telemetry.addData("Pose Rotation", odometry.get().getPose().getRotation().getDegrees());
    }

    @Override
    public void cleanup(@NonNull Wrapper opMode) {
        // remove all refs
        left.invalidate();
        right.invalidate();
        perp.invalidate();
    }

    // TODO: convert to dairy pose2d
    public Pose2d getPose() {
        return odometry.get().getPose();
    }

}
