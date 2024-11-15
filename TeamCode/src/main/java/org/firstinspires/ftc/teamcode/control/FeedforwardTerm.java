package org.firstinspires.ftc.teamcode.control;

import androidx.annotation.NonNull;

import dev.frozenmilk.dairy.core.util.controller.calculation.ControllerCalculation;
import dev.frozenmilk.dairy.core.util.supplier.numeric.EnhancedDoubleSupplier;
import dev.frozenmilk.dairy.core.util.supplier.numeric.MotionComponentSupplier;

public class FeedforwardTerm implements ControllerCalculation<Double> {
    public enum Type {
        SINE,
        COSINE,
        NORMAL
    }

    private final Type type;
    private final EnhancedDoubleSupplier operandSupplier;
    private final double kConstant;

    /*
        WARNING: cosine/sine feedforwards are not automatically converted to degrees!
     */
    public FeedforwardTerm(Type type, EnhancedDoubleSupplier operandSupplier, double constant) {
        this.type = type;
        this.operandSupplier = operandSupplier;
        kConstant = constant;
    }

    @NonNull
    @Override
    public Double evaluate(@NonNull Double accumulation,
                           @NonNull MotionComponentSupplier<? extends Double> state,
                           @NonNull MotionComponentSupplier<? extends Double> target,
                           @NonNull MotionComponentSupplier<? extends Double> error,
                           double deltaTime) {
        double result;
        switch (type) {
            case SINE:
                result = Math.sin(operandSupplier.state()) * kConstant;
                break;
            case COSINE:
                result = Math.cos(operandSupplier.state()) * kConstant;
                break;
            default:
                result = kConstant * operandSupplier.state();
                break;
        }

        if (Double.isNaN(result)) {
            return accumulation;
        }

        return accumulation + result;
    }

    @Override
    public void reset() {

    }

    @Override
    public void update(@NonNull Double accumulation,
                       @NonNull MotionComponentSupplier<? extends Double> state,
                       @NonNull MotionComponentSupplier<? extends Double> target,
                       @NonNull MotionComponentSupplier<? extends Double> error, double deltaTime) {

    }
}
