package org.firstinspires.ftc.teamcode.feature.subsystem;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.lynx.LynxModule;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import dev.frozenmilk.dairy.core.dependency.Dependency;
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation;
import dev.frozenmilk.dairy.core.wrapper.Wrapper;
import dev.frozenmilk.mercurial.subsystems.Subsystem;
import kotlin.annotation.MustBeDocumented;

// TODO: SubsystemObjectCell
public class Hubs implements Subsystem {
    private Dependency<?> dependencies = DEFAULT_DEPENDENCY
            .and(new SingleAnnotation<>(Attach.class));
    public static Hubs INSTANCE = new Hubs();

    private List<LynxModule> lynxModules;

    private Hubs() {

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
        lynxModules =  opmode.getOpMode().hardwareMap.getAll(LynxModule.class);
        lynxModules.forEach(x -> x.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));
        lynxModules.forEach(LynxModule::clearBulkCache);
    }

    @Override
    public void preUserStartHook(@NonNull Wrapper opmode) {
        lynxModules.forEach(LynxModule::clearBulkCache);
    }

    @Override
    public void preUserLoopHook(@NonNull Wrapper opmode) {
        lynxModules.forEach(LynxModule::clearBulkCache);
    }

    @Override
    public void cleanup(@NonNull Wrapper opMode) {
        // remove all refs
        lynxModules = null;
    }

}
