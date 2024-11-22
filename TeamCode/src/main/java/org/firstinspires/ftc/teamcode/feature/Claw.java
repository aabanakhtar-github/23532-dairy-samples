package org.firstinspires.ftc.teamcode.feature;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.feature.subsystem.Hubs;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import dev.frozenmilk.dairy.core.FeatureRegistrar;
import dev.frozenmilk.dairy.core.dependency.Dependency;
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation;
import dev.frozenmilk.dairy.core.wrapper.Wrapper;
import dev.frozenmilk.mercurial.commands.Lambda;
import dev.frozenmilk.mercurial.subsystems.Subsystem;
import dev.frozenmilk.mercurial.subsystems.SubsystemObjectCell;
import kotlin.annotation.MustBeDocumented;

@Config
// TODO: cache position servo writes
public class Claw implements Subsystem {

    private Dependency<?> dependencies = DEFAULT_DEPENDENCY
            .and(new SingleAnnotation<>(Hubs.Attach.class));

    public static Claw INSTANCE = new Claw();

    @Config
    public static class Constants {
        public static double diffyLIntake = 0.0;
        public static double diffyRIntake = 0.0;
        public static double diffyLOuttake = 0.0;
        public static double diffyROuttake = 0.0;
        public static double clawOpen = 0.0;
        public static double clawClose = 0.5;
    }

    private SubsystemObjectCell<Servo> diffyLeft = subsystemCell(() -> FeatureRegistrar.getActiveOpMode()
            .hardwareMap.get(Servo.class, "diffyL"));
    private SubsystemObjectCell<Servo> diffyRight = subsystemCell(() -> FeatureRegistrar.getActiveOpMode()
            .hardwareMap.get(Servo.class, "diffyL"));
    private SubsystemObjectCell<Servo> claw = subsystemCell(() -> FeatureRegistrar.getActiveOpMode()
            .hardwareMap.get(Servo.class, "claw"));

    private Claw() {

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

    public Servo getDiffyL() {
        return diffyLeft.get();
    }

    public Servo getDiffyR() {
        return diffyRight.get();
    }

    public Servo getClaw() {
        return claw.get();
    }

    @Override
    public void preUserInitHook(@NonNull Wrapper opMode) {
       getDiffyL().setPosition(Constants.diffyLOuttake);
       getDiffyL().setPosition(Constants.diffyLOuttake);
       getClaw().setPosition(Constants.clawClose);
    }

    @Override
    public void cleanup(@NonNull Wrapper opMode) {
        diffyLeft.invalidate();
        diffyRight.invalidate();
        claw.invalidate();
    }

    public Lambda prepareIntake() {
        return new Lambda("prepare-intake")
                .setInit(() -> {})
                .setExecute(() -> {
                    getDiffyL().setPosition(Constants.diffyLIntake);
                    getDiffyL().setPosition(Constants.diffyLIntake);
                    getClaw().setPosition(Constants.clawOpen);
                })
                .setFinish(() -> true);
    }


    public Lambda prepareOuttake() {
        return new Lambda("prepare-outtake")
                .setInit(() -> {})
                .setExecute(() -> {
                    getClaw().setPosition(Constants.clawClose);
                    getDiffyL().setPosition(Constants.diffyLOuttake);
                    getDiffyL().setPosition(Constants.diffyLOuttake);
                })
                .setFinish(() -> true);
    }

}
