package net.ninjadev.freelook.config;


import com.google.gson.annotations.Expose;
import net.ninjadev.freelook.config.option.Option;

public class FreeLookConfiguration extends BaseConfig {

    @Expose private Option.BooleanValue clampView;
    @Expose private Option.BooleanValue interpolate;
    @Expose private Option.DoubleValue interpolateSpeed;

    @Override
    public String getName() {
        return "FreeLook";
    }

    @Override
    protected void reset() {
        clampView = new Option.BooleanValue(true, "Clamp your head rotation to your shoulders. As you would expect in real life.");
        interpolate = new Option.BooleanValue(true, "Smooth the camera returning to original direction. Instead of snapping back instantly.");
        interpolateSpeed = new Option.DoubleValue(0.2D, "The time in seconds to move your view back to the original position.");
    }

    public boolean shouldClamp() {
        return this.clampView.getValue();
    }

    public boolean shouldInterpolate() {
        return this.interpolate.getValue();
    }

    public double getInterpolateSpeed() {
        return this.interpolateSpeed.getValue();
    }

}
