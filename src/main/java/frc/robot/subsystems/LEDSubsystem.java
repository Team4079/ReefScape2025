package frc.robot.subsystems;


import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.utils.RobotParameters.SwerveParameters.Thresholds.*;

public class LEDSubsystem extends SubsystemBase {
    private final AddressableLED alignmentIndication1;
    private final AddressableLEDBuffer addressableLEDBuffer;

    /**
     * The Singleton instance of this LEDSubsystem. Code should use
     * the {@link #getInstance()} method to get the single instance (rather
     * than trying to construct an instance of this class.)
     */
    private static final LEDSubsystem INSTANCE = new LEDSubsystem();

    /**
     * Returns the Singleton instance of this LEDSubsystem. This static method
     * should be used, rather than the constructor, to get the single instance
     * of this class. For example: {@code LEDSubsystem.getInstance();}
     */
    @SuppressWarnings("WeakerAccess")
    public static LEDSubsystem getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new instance of this LEDSubsystem. This constructor
     * is private since this class is a Singleton. Code should use
     * the {@link #getInstance()} method to get the singleton instance.
     */
    private LEDSubsystem() {
        alignmentIndication1 = new AddressableLED(9);
        addressableLEDBuffer = new AddressableLEDBuffer(120);
        alignmentIndication1.setLength(addressableLEDBuffer.getLength());
        alignmentIndication1.setData(addressableLEDBuffer);
        alignmentIndication1.start();
    }

    /**
     * This method will be called once per scheduler run. Updates the LED pattern based on the robot
     * state.
     */
    @Override
    public void periodic() {
        if (RobotState.isDisabled()) {
            highTideFlow();
        }
    }

    /**
     * Sets the color for each of the LEDs based on RGB values.
     *
     * @param r (Red) Integer values between 0 - 255
     * @param g (Green) Integer values between 0 - 255
     * @param b (Blue) Integer values between 0 - 255
     */
    public void setRGB(int r, int g, int b) {
        for (int i = 0; i < addressableLEDBuffer.getLength(); i++) {
            addressableLEDBuffer.setRGB(i, r, g, b);
        }
        alignmentIndication1.setData(addressableLEDBuffer);
    }

    /**
     * Sets the color for each of the LEDs based on HSV values
     *
     * @param h (Hue) Integer values between 0 - 180
     * @param s (Saturation) Integer values between 0 - 255
     * @param v (Value) Integer values between 0 - 255
     */
    public void rainbowHSV(int h, int s, int v) {
        for (int i = 0; i < addressableLEDBuffer.getLength(); i++) {
            addressableLEDBuffer.setHSV(i, h, s, v);
        }
        alignmentIndication1.setData(addressableLEDBuffer);
    }

    /** Sets the LED color to tan. */
    public void setTan() {
        setRGB(TAN_LED[0], TAN_LED[1], TAN_LED[2]);
    }

    /** Sets the LED color to red. */
    public void setRed() {
        setRGB(RED_LED[0], RED_LED[1], RED_LED[2]);
    }

    /** Sets the LED color to green. */
    public void setGreen() {
        setRGB(GREEN_LED[0], GREEN_LED[1], GREEN_LED[2]);
    }

    /**
     * Sets the LED color to orange. This is a specific shade of orange that is used for the LED strip.
     */
    public void setOrange() {
        setRGB(ORANGE_LED[0], ORANGE_LED[1], ORANGE_LED[2]);
    }

    /** Sets the LED color to purple. */
    public void setPurpleColor() {
        setRGB(PURPLE_LED[0], PURPLE_LED[1], PURPLE_LED[2]);
    }

    /** Sets the LED color to high tide (a specific shade of blue-green). */
    public void setHighTide() {
        setRGB(HIGHTIDE_LED[0], HIGHTIDE_LED[1], HIGHTIDE_LED[2]);
    }

    /**
     * Creates a flowing high tide effect on the LED strip. The effect is based on a sine wave pattern
     * that changes over time.
     */
    public void highTideFlow() {
        long currentTime = System.currentTimeMillis();
        int length = addressableLEDBuffer.getLength();

        final int waveSpeed = 30;
        final int waveWidth = 55;

        for (int i = 0; i < length; i++) {
            double wave =
                    Math.sin((i + ((double) currentTime / waveSpeed)) % length * (2 * Math.PI / waveWidth));

            wave = (wave + 1) / 2;

            int r = (int) (wave * 0);
            int g = (int) (wave * 200);
            int b = (int) (wave * 50);

            addressableLEDBuffer.setRGB(i, r, g, b);
        }
        alignmentIndication1.setData(addressableLEDBuffer);
    }
}
