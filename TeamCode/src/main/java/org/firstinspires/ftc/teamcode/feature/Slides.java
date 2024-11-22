package org.firstinspires.ftc.teamcode.feature;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.control.FeedforwardTerm;
import org.firstinspires.ftc.teamcode.feature.subsystem.Hubs;
import org.firstinspires.ftc.teamcode.feature.subsystem.PitchingArm;
import org.firstinspires.ftc.teamcode.wrapper.CachedMotor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import dev.frozenmilk.dairy.core.Feature;
import dev.frozenmilk.dairy.core.FeatureRegistrar;
import dev.frozenmilk.dairy.core.dependency.Dependency;
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation;
import dev.frozenmilk.dairy.core.util.controller.calculation.pid.DoubleComponent;
import dev.frozenmilk.dairy.core.util.controller.implementation.DoubleController;
import dev.frozenmilk.dairy.core.util.supplier.numeric.CachedMotionComponentSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.EnhancedDoubleSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.MotionComponents;
import dev.frozenmilk.dairy.core.wrapper.Wrapper;
import dev.frozenmilk.mercurial.commands.Lambda;
import dev.frozenmilk.mercurial.subsystems.Subsystem;
import dev.frozenmilk.mercurial.subsystems.SubsystemObjectCell;
import kotlin.annotation.MustBeDocumented;

@Config
public class Slides implements Subsystem {
    private Dependency<?> dependencies = DEFAULT_DEPENDENCY
            .and(new SingleAnnotation<>(Attach.class));

    public static Slides INSTANCE = new Slides();
    public double targetPosition = Constants.DEFAULT_POSITON;

    @com.acmerobotics.dashboard.config.Config
    public static class Constants {
       public static double MAX_EXTENSION = 0.0;
       public static double kP = 0.0;
       public static double kD = 0.0;
       public static double kSin = 0.0;
       public static double DEFAULT_POSITON = 0.0;
    }

    private SubsystemObjectCell<CachedMotor> slideLeft =
            subsystemCell(() -> new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "slideL"));
    private SubsystemObjectCell<CachedMotor> slideRight =
            subsystemCell(() -> new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "slideR"));

    // PID and Feedforward
    private final DoubleController slidePID = new DoubleController(
            component -> {
                if (component == MotionComponents.STATE) {
                    return targetPosition;
                }
                return Double.NaN;
            },
            new EnhancedDoubleSupplier(this::getExtension),
            new CachedMotionComponentSupplier<Double>(
                    component -> {
                        if (component == MotionComponents.STATE) {
                            return 15.0; // default tolerance
                        }
                        return Double.NaN;
                    }
            ),
            this::setPower,
            new DoubleComponent.P(MotionComponents.STATE, Constants.kP)
                    .plus(new DoubleComponent.D(MotionComponents.STATE, Constants.kD))
                    .plus(new FeedforwardTerm(FeedforwardTerm.Type.SINE, new EnhancedDoubleSupplier(() -> {
                        return Math.toRadians(PitchingArm.INSTANCE.getLastAngle());
                    }), Constants.kSin))
    );

    private Slides() {
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

    public CachedMotor getLeftMotor() {
        return slideLeft.get();
    }

    public CachedMotor getRightMotor() {
       return slideRight.get();
    }

    public double getExtension() {
       return getLeftMotor().__IMPL.getCurrentPosition();
    }

    public double getExtensionRatio() {
       return getLeftMotor().__IMPL.getCurrentPosition() / Constants.MAX_EXTENSION;
    }

    public void setPosition(double pos) {
        targetPosition = pos;
    }

    private void setPower(double power) {
        getLeftMotor().setPower(power);
        getRightMotor().setPower(power);
    }

    @Override
    public void preUserInitHook(@NonNull Wrapper opmode) {
       getLeftMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       getRightMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       getLeftMotor().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       getRightMotor().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       slidePID.setEnabled(true);
    }
}
