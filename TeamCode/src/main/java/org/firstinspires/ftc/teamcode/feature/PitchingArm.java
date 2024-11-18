package org.firstinspires.ftc.teamcode.feature.subsystem;

import android.provider.SyncStateContract;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.control.FeedforwardTerm;
import org.firstinspires.ftc.teamcode.wrapper.CachedMotor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.frozenmilk.dairy.core.FeatureRegistrar;
import dev.frozenmilk.dairy.core.dependency.Dependency;
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation;
import dev.frozenmilk.dairy.core.util.controller.calculation.ControllerCalculation;
import dev.frozenmilk.dairy.core.util.controller.calculation.pid.DoubleComponent;
import dev.frozenmilk.dairy.core.util.controller.implementation.DoubleController;
import dev.frozenmilk.dairy.core.util.supplier.numeric.CachedMotionComponentSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.EnhancedDoubleSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.MotionComponentSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.MotionComponents;
import dev.frozenmilk.dairy.core.wrapper.Wrapper;
import dev.frozenmilk.mercurial.commands.Lambda;
import dev.frozenmilk.mercurial.subsystems.Subsystem;
import dev.frozenmilk.mercurial.subsystems.SubsystemObjectCell;
import kotlin.annotation.MustBeDocumented;

public class PitchingArm implements Subsystem {
    @Config
    public static class Constants {
        // 72:1 ratio PPR -> 1993.6 pulses
        public static double kTicksPerDegree = 1993.6 / 360.0;
        public static double kP = 0.0,
                kI = 0.0,
                kD = 0.0,
                kCos = 0.0,
                kExtension = 0.0;
    }

    private Dependency<?> dependencies = DEFAULT_DEPENDENCY
            .and(new SingleAnnotation<>(Attach.class));
    public static PitchingArm INSTANCE = new PitchingArm();

    // angle to persist state
    private static double lastEncoderPosition = 0.0f;
    private static double targetPosition = 0.0f;
    // motor
    private final SubsystemObjectCell<CachedMotor> pitchMotor = subsystemCell(
            () -> new CachedMotor(FeatureRegistrar.getActiveOpMode().hardwareMap, "pitcher"));
    // PID and Feedforward
    private final DoubleController pitchPID = new DoubleController(
            component -> {
                    if (component == MotionComponents.STATE)
                        return getTargetPosition();
                return Double.NaN;
            },
            new EnhancedDoubleSupplier(() -> (double)getPitchMotor().__IMPL.getCurrentPosition()),
            new CachedMotionComponentSupplier<Double>(
                    component -> {
                        if (component == MotionComponents.STATE) {
                            return 15.0; // default tolerance
                        }
                        return Double.NaN;
                    }
            ),
            getPitchMotor()::setPower,
            new DoubleComponent.P(MotionComponents.STATE, Constants.kP)
                    .plus(new DoubleComponent.I(MotionComponents.STATE, Constants.kI))
                    .plus(new DoubleComponent.D(MotionComponents.STATE, Constants.kD))
                    .plus(new FeedforwardTerm(FeedforwardTerm.Type.COSINE, new EnhancedDoubleSupplier(() -> {
                        return Math.toRadians(getLastAngle());
                    }), Constants.kCos))
                    /*.plus(new FeedforwardTerm(FeedforwardTerm.Type.NORMAL, new EnhancedDoubleSupplier(() -> {
                        return amogus(); // TODO: fix
                    }), Constants.kExtension))*/
    );


    private PitchingArm() {
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
    public void preUserInitHook(@NonNull Wrapper opmode) {
        getPitchMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        getPitchMotor().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void preUserStartHook(@NonNull Wrapper opmode) {
        // TODO: current based arm reset
    }

    @Override
    public void preUserLoopHook(@NonNull Wrapper opmode) {
        lastEncoderPosition = getTargetPosition();
    }


    @Override
    public void cleanup(@NonNull Wrapper opMode) {
        // remove all refs
        pitchMotor.invalidate();
    }

    public CachedMotor getPitchMotor() {
        return pitchMotor.get();
    }

    public double getLastAngle() {
        return lastEncoderPosition / Constants.kTicksPerDegree;
    }

    public double getTargetPosition() {
        return targetPosition;
    }

    // angle in degrees
    public Lambda setAngleCommand(double angle) {
        return new Lambda("set-angle-command")
                .setInit(() -> {})
                .setExecute(() -> {
                   setTargetPosition(Constants.kTicksPerDegree * angle);
                })
                .setFinish(() -> true);
    }

    private double setTargetPosition(double pos) {
        return targetPosition = pos;
    }
}