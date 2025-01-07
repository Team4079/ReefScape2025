package frc.robot.subsystems;

import static frc.robot.utils.Dash.*;

import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.*;
import com.ctre.phoenix6.signals.*;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.utils.*;
import frc.robot.utils.RobotParameters.SwerveParameters.*;

public class ElevatorSubsystem extends SubsystemBase {
  private TalonFX elevatorMotorLeft;
  private TalonFX elevatorMotorRight;

  private TalonFXConfigurator elevatorLeftConfigurator;
  private TalonFXConfigurator elevatorRightConfigurator;

  private TalonFXConfiguration elevatorLeftConfiguration;
  private TalonFXConfiguration elevatorRightConfiguration;

  private Slot0Configs elevatorLeftConfigs;
  private Slot0Configs elevatorRightConfigs;

  private PositionVoltage pos_reqest;
  private VelocityVoltage vel_voltage;

  private MotorOutputConfigs elevatorConfigs;

  private CurrentLimitsConfigs leftMotorCurrentConfig;
  private CurrentLimitsConfigs rightMotorCurrentConfig;

  private ClosedLoopRampsConfigs leftMotorRampConfig;
  private ClosedLoopRampsConfigs rightMotorRampConfig;

  private SoftwareLimitSwitchConfigs leftSoftLimitConfig;
  private SoftwareLimitSwitchConfigs rightSoftLimitConfig;

  private VoltageOut voltageOut;

  private double deadband = 0.001;

    /**
   * The Singleton instance of this ElevatorSubsystem. Code should use the {@link #getInstance()}
   * method to get the single instance (rather than trying to construct an instance of this class.)
   */
  private static final ElevatorSubsystem INSTANCE = new ElevatorSubsystem();

  /**
   * Returns the Singleton instance of this ElevatorSubsystem. This static method should be used,
   * rather than the constructor, to get the single instance of this class. For example: {@code
   * ElevatorSubsystem.getInstance();}
   */
  @SuppressWarnings("WeakerAccess")
  public static ElevatorSubsystem getInstance() {
    return INSTANCE;
  }

  /**
   * Creates a new instance of this ElevatorSubsystem. This constructor is private since this class
   * is a Singleton. Code should use the {@link #getInstance()} method to get the singleton
   * instance.
   */
  private ElevatorSubsystem() {
    elevatorMotorLeft = new TalonFX(RobotParameters.MotorParameters.ELEVATOR_MOTOR_LEFT_ID);
    elevatorMotorRight = new TalonFX(RobotParameters.MotorParameters.ELEVATOR_MOTOR_RIGHT_ID);

    elevatorConfigs = new MotorOutputConfigs();

    elevatorLeftConfigurator = elevatorMotorLeft.getConfigurator();
    elevatorRightConfigurator = elevatorMotorRight.getConfigurator();

    elevatorLeftConfigs = new Slot0Configs();
    elevatorRightConfigs = new Slot0Configs();

    elevatorLeftConfiguration = new TalonFXConfiguration();
    elevatorRightConfiguration = new TalonFXConfiguration();

    elevatorMotorLeft.getConfigurator().apply(elevatorLeftConfiguration);
    elevatorMotorRight.getConfigurator().apply(elevatorRightConfiguration);

    elevatorConfigs.NeutralMode = NeutralModeValue.Brake;
    elevatorLeftConfigurator.apply(elevatorConfigs);
    elevatorRightConfigurator.apply(elevatorConfigs);

    elevatorLeftConfigs.kP = RobotParameters.ElevatorParameters.ELEVATOR_PID_LEFT_P;
    elevatorLeftConfigs.kI = RobotParameters.ElevatorParameters.ELEVATOR_PID_LEFT_I;
    elevatorLeftConfigs.kD = RobotParameters.ElevatorParameters.ELEVATOR_PID_LEFT_D;
    elevatorLeftConfigs.kV = RobotParameters.ElevatorParameters.ELEVATOR_PID_LEFT_V;

    elevatorRightConfigs.kP = RobotParameters.ElevatorParameters.ELEVATOR_PID_RIGHT_P;
    elevatorRightConfigs.kI = RobotParameters.ElevatorParameters.ELEVATOR_PID_RIGHT_I;
    elevatorRightConfigs.kD = RobotParameters.ElevatorParameters.ELEVATOR_PID_RIGHT_D;
    elevatorRightConfigs.kV = RobotParameters.ElevatorParameters.ELEVATOR_PID_RIGHT_V;

    elevatorMotorLeft.getConfigurator().apply(elevatorLeftConfigs);
    elevatorMotorRight.getConfigurator().apply(elevatorRightConfigs);

    leftMotorCurrentConfig = new CurrentLimitsConfigs();
    rightMotorCurrentConfig = new CurrentLimitsConfigs();

    leftMotorRampConfig = new ClosedLoopRampsConfigs();
    rightMotorRampConfig = new ClosedLoopRampsConfigs();

    leftSoftLimitConfig = new SoftwareLimitSwitchConfigs();
    rightSoftLimitConfig = new SoftwareLimitSwitchConfigs();

    leftMotorCurrentConfig.SupplyCurrentLimit = 100;
    leftMotorCurrentConfig.SupplyCurrentLimitEnable = true;
    leftMotorCurrentConfig.StatorCurrentLimit = 100;
    leftMotorCurrentConfig.StatorCurrentLimitEnable = true;

    rightMotorCurrentConfig.SupplyCurrentLimit = 100;
    rightMotorCurrentConfig.SupplyCurrentLimitEnable = true;
    rightMotorCurrentConfig.StatorCurrentLimit = 100;
    rightMotorCurrentConfig.StatorCurrentLimitEnable = true;

    elevatorMotorLeft.getConfigurator().apply(leftMotorCurrentConfig);
    elevatorMotorRight.getConfigurator().apply(rightMotorCurrentConfig);

    leftMotorRampConfig.DutyCycleClosedLoopRampPeriod = 0.1;
    rightMotorRampConfig.DutyCycleClosedLoopRampPeriod = 0.1;

    elevatorMotorLeft.getConfigurator().apply(leftMotorRampConfig);
    elevatorMotorRight.getConfigurator().apply(rightMotorRampConfig);

    // on
    leftSoftLimitConfig.ForwardSoftLimitEnable = true;
    leftSoftLimitConfig.ReverseSoftLimitEnable = true;
    leftSoftLimitConfig.ForwardSoftLimitThreshold = 40;
    leftSoftLimitConfig.ReverseSoftLimitThreshold = 0.2;

    rightSoftLimitConfig.ForwardSoftLimitEnable = true;
    rightSoftLimitConfig.ReverseSoftLimitEnable = true;
    rightSoftLimitConfig.ForwardSoftLimitThreshold = 40;
    rightSoftLimitConfig.ReverseSoftLimitThreshold = 0.2;

    elevatorLeftConfiguration.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
    elevatorRightConfiguration.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

    elevatorLeftConfiguration.SoftwareLimitSwitch = leftSoftLimitConfig;
    elevatorRightConfiguration.SoftwareLimitSwitch = rightSoftLimitConfig;

    elevatorMotorLeft.getConfigurator().apply(leftSoftLimitConfig);
    elevatorMotorRight.getConfigurator().apply(rightSoftLimitConfig);

    // absoluteEncoder = new DigitalInput(9);

    vel_voltage = new VelocityVoltage(0);
    pos_reqest = new PositionVoltage(0);
    voltageOut = new VoltageOut(0);
    new PositionDutyCycle(0);

    elevatorMotorLeft.setPosition(0);
    elevatorMotorRight.setPosition(0);
  }

  // This method will be called once per scheduler run
  @Override
  public void periodic() {
    // absPos = absoluteEncoder.getPosition();
    // SmartDashboard.putNumber("Absolute Encoder Position", getAbsoluteEncoder());
    if (Thresholds.TEST_MODE) {
      SmartDashboard.putNumber(
          "Elevator Left Position", elevatorMotorLeft.getPosition().getValueAsDouble());
      SmartDashboard.putNumber(
          "Elevator Right Position", elevatorMotorRight.getPosition().getValueAsDouble());
      SmartDashboard.putBoolean("Elevator SoftLimit", getSoftLimitBoolean());
      // SmartDashboard.putBoolean("limit", limit);
      dash(
          pairOf("Elevator Left Position", elevatorMotorLeft.getPosition().getValueAsDouble()),
          pairOf("Elevator Right Position", elevatorMotorRight.getPosition().getValueAsDouble()),
          pairOf("Elevator SoftLimit", getSoftLimitBoolean()));
    }

    // TODO: wtf does this do, make it do something useful or remove it
    // if (absPos == ElevatorConstants.ELEVATOR_NEUTRAL_POS) {
    //   ElevatorConstants.IS_NEUTRAL = true;
    // }
  }

  /** Stops the elevator motors */
  public void stopMotors() {
    elevatorMotorLeft.stopMotor();
    elevatorMotorRight.stopMotor();
    voltageOut.Output = -0.014;
    elevatorMotorLeft.setControl(voltageOut);
    elevatorMotorRight.setControl(voltageOut);
  }

  /**
   * Set the position of the left and right elevator motors
   *
   * @param left Left motor position
   * @param right Right motor position
   */
  public void setMotorPosition(double left, double right) {
    elevatorMotorLeft.setControl(pos_reqest.withPosition(left));
    elevatorMotorRight.setControl(pos_reqest.withPosition(right));
  }

  /**
   * Get the position of the elevator motor
   *
   * @param motor The motor to get the position of
   * @return double, the position of the elevator motor
   */
  // TODO: Figure out what the .magnitude() method does and document it in this file
  public double getElevatorPosValue(String motor) {
    if (motor.equals("left")) {
      return elevatorMotorLeft.getPosition().getValue().magnitude();
    } else if (motor.equals("right")) {
      return elevatorMotorRight.getPosition().getValue().magnitude();
    } else {
      // This only happens if the string is not "left" or "right"
      return 0.0;
    }
  }

  public double getElevatorPosAvg() {
    return (getElevatorPosValue("left") + getElevatorPosValue("right")) / 2;
  }

  public void resetEncoders() {
    elevatorMotorLeft.setPosition(0);
    elevatorMotorRight.setPosition(0);
  }

  public void toggleSoftStop() {
    RobotParameters.ElevatorParameters.SOFT_LIMIT_ENABLED =
        !RobotParameters.ElevatorParameters.SOFT_LIMIT_ENABLED;
    leftSoftLimitConfig.ReverseSoftLimitEnable =
        RobotParameters.ElevatorParameters.SOFT_LIMIT_ENABLED;
    // leftSoftLimitConfig.ForwardSoftLimitThreshold = 1100;
    leftSoftLimitConfig.ReverseSoftLimitThreshold = 0;

    // rightSoftLimitConfig.ForwardSoftLimitEnable = elevatorGlobalValues.soft_limit_enabled;
    rightSoftLimitConfig.ReverseSoftLimitEnable =
        RobotParameters.ElevatorParameters.SOFT_LIMIT_ENABLED;
    // rightSoftLimitConfig.ForwardSoftLimitThreshold = 1100;
    rightSoftLimitConfig.ReverseSoftLimitThreshold = 0;

    elevatorMotorLeft.getConfigurator().apply(leftSoftLimitConfig);
    elevatorMotorRight.getConfigurator().apply(rightSoftLimitConfig);
  }

  /**
   * Get the absolute encoder position // @return double, the absolute encoder position of the
   * elevator motor
   */
  // public double getAbsoluteEncoder() {
  //   // return actualAbsEnc.getAbsolutePosition() * 2048;
  //   if (absoluteEncoder.getPosition() > 190) {
  //     return 0;
  //   } else {
  //     return absoluteEncoder.getPosition();
  //   }
  // }

  public void moveElevator(double velocity) {
    if (Math.abs(velocity) >= deadband) {
      elevatorMotorLeft.setControl(vel_voltage.withVelocity(velocity * 500 * 0.75));
      elevatorMotorRight.setControl(vel_voltage.withVelocity(velocity * 500 * 0.75));

      SmartDashboard.putNumber("ElevatorLeft Velo Error", elevatorMotorLeft.get() - velocity);
      SmartDashboard.putNumber("ElevatorRight Velo Error", elevatorMotorRight.get() - velocity);
    } else {
      stopMotors();
    }
  }

  public void setElevator(double pos) {
    elevatorMotorLeft.setControl(vel_voltage.withVelocity(pos));
    elevatorMotorRight.setControl(vel_voltage.withVelocity(pos));
  }

  public void toggleLimit() {
    RobotParameters.ElevatorParameters.IS_SOFTLIMIT =
        !RobotParameters.ElevatorParameters.IS_SOFTLIMIT;
  }

  // public void recalibrateEncoders() {
  //   ElevatorGlobalValues.offset = ElevatorGlobalValues.Elevator_NEUTRAL_ANGLE -
  // getAbsoluteEncoder();
  // }

  public boolean getSoftLimitBoolean() {
    return RobotParameters.ElevatorParameters.IS_SOFTLIMIT;
  }
}
