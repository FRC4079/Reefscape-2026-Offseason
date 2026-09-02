// package frc.robot.subsystems.drive
//
// import com.ctre.phoenix6.CANBus.systemcore
// import com.ctre.phoenix6.configs.CANcoderConfiguration
// import com.ctre.phoenix6.configs.TalonFXConfiguration
// import com.ctre.phoenix6.configs.TorqueCurrentConfigs
// import com.ctre.phoenix6.controls.PositionTorqueCurrentFOC
// import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC
// import com.ctre.phoenix6.hardware.CANcoder
// import com.ctre.phoenix6.hardware.TalonFX
// import com.ctre.phoenix6.signals.FeedbackSensorSourceValue
// import com.ctre.phoenix6.signals.NeutralModeValue
// import com.ctre.phoenix6.signals.SensorDirectionValue
// import frc.robot.utils.RobotParameters.MotorParameters
// import frc.robot.utils.RobotParameters.SwerveParameters
// import frc.robot.utils.RobotParameters.SwerveParameters.PIDParameters
// import org.littletonrobotics.junction.networktables.LoggedNetworkNumber
// import org.wpilib.driverstation.Alert
// import org.wpilib.math.controller.PIDController
// import org.wpilib.math.geometry.Rotation2d
// import org.wpilib.math.kinematics.SwerveModulePosition
// import org.wpilib.math.kinematics.SwerveModuleVelocity
// import java.util.logging.Level
//
// class SwerveModule(
//    driveId: Int,
//    steerId: Int,
//    canCoderID: Int,
//    canCoderDriveStraightSteerSetPoint: Double,
// ) {
//    private val driveMotor: TalonFX = TalonFX(driveId, systemcore(1))
//    private val canCoder: CANcoder = CANcoder(canCoderID, systemcore(1))
//    private val steerMotor: TalonFX = TalonFX(steerId, systemcore(1))
//    private val positionSetter: PositionTorqueCurrentFOC = PositionTorqueCurrentFOC(0.0)
//    private val velocitySetter: VelocityTorqueCurrentFOC = VelocityTorqueCurrentFOC(0.0)
//    private var driveVelocity: Double = 0.0
//    private var drivePosition: Double = 0.0
//    private var steerPosition: Double = 0.0
//    private var steerVelocity: Double = 0.0
//    private val driveConfigs: TalonFXConfiguration = TalonFXConfiguration()
//    private val steerConfigs: TalonFXConfiguration
//    private val driveTorqueConfigs: TorqueCurrentConfigs
//    private lateinit var driveP: LoggedNetworkNumber
//    private lateinit var driveI: LoggedNetworkNumber
//    private lateinit var driveD: LoggedNetworkNumber
//    private lateinit var driveV: LoggedNetworkNumber
//
//    private lateinit var steerP: LoggedNetworkNumber
//    private lateinit var steerI: LoggedNetworkNumber
//    private lateinit var steerD: LoggedNetworkNumber
//    private lateinit var steerV: LoggedNetworkNumber
//
//    private lateinit var driveDisconnectedAlert: Alert
//    private lateinit var turnDisconnectedAlert: Alert
//    private lateinit var canCoderDisconnectedAlert: Alert
//
//    /**
//     * Current measured position — constructs a fresh SwerveModulePosition every call.
//     */
//    val position: SwerveModulePosition
//        get() {
//            driveVelocity = driveMotor.velocity.valueAsDouble
//            drivePosition = driveMotor.position.valueAsDouble
//            steerVelocity = steerMotor.velocity.valueAsDouble
//            steerPosition = steerMotor.position.valueAsDouble
//
//            val angle = Rotation2d.fromRotations(canCoder.absolutePosition.valueAsDouble)
//            val distance = drivePosition / MotorParameters.DRIVE_MOTOR_GEAR_RATIO * MotorParameters.METERS_PER_REV
//            return SwerveModulePosition(distance, angle)
//        }
//
//    /**
//     * Current measured velocity and angle — fresh reading every call.
//     */
//    val velocity: SwerveModuleVelocity
//        get() {
//            val speed =
//                driveMotor.rotorVelocity.valueAsDouble /
//                    MotorParameters.DRIVE_MOTOR_GEAR_RATIO *
//                    MotorParameters.METERS_PER_REV
//            val angle = Rotation2d.fromRotations(canCoder.absolutePosition.valueAsDouble)
//            return SwerveModuleVelocity(speed, angle)
//        }
//
//    /**
//     * Commands the module to a desired velocity/angle.
//     * optimize() returns a new object in 2027
//     */
//    fun setDesiredVelocity(desired: SwerveModuleVelocity) {
//        val currentAngle = Rotation2d.fromRotations(canCoder.absolutePosition.valueAsDouble)
//
//        // optimize() is immutable — returns a new SwerveModuleVelocity
//        val optimized = desired.optimize(currentAngle)
//
//        // Command steer
//        val angleToSet = optimized.angle.rotations
//        steerMotor.setControl(positionSetter.withPosition(angleToSet))
//
//        // Command drive
//        val velocityToSet =
//            optimized.velocity *
//                (MotorParameters.DRIVE_MOTOR_GEAR_RATIO / MotorParameters.METERS_PER_REV)
//        driveMotor.setControl(velocitySetter.withVelocity(velocityToSet))
//
// //        "drive actual speed " + canCoder.deviceID log driveMotor.velocity.valueAsDouble
// //        "drive set speed " + canCoder.deviceID log velocityToSet
// //        "steer actual angle " + canCoder.deviceID log canCoder.absolutePosition.valueAsDouble
// //        "steer set angle " + canCoder.deviceID log angleToSet
// //        "desired state after optimize " + canCoder.deviceID log optimized.angle.rotations
//    }
//
//    init {
//        // Drive motor config
//        driveConfigs.Slot0.kP = PIDParameters.DRIVE_PID_AUTO.p
//        driveConfigs.Slot0.kI = PIDParameters.DRIVE_PID_AUTO.i
//        driveConfigs.Slot0.kD = PIDParameters.DRIVE_PID_AUTO.d
//        driveConfigs.Slot0.kV = PIDParameters.DRIVE_PID_AUTO.v
//
//        driveConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake
//        driveConfigs.MotorOutput.Inverted = SwerveParameters.Thresholds.DRIVE_MOTOR_INVERTED
//        driveConfigs.CurrentLimits.SupplyCurrentLimit = MotorParameters.DRIVE_SUPPLY_LIMIT
//        driveConfigs.CurrentLimits.SupplyCurrentLimitEnable = true
//        driveConfigs.CurrentLimits.StatorCurrentLimit = MotorParameters.DRIVE_STATOR_LIMIT
//        driveConfigs.CurrentLimits.StatorCurrentLimitEnable = true
//        driveConfigs.Feedback.RotorToSensorRatio = MotorParameters.DRIVE_MOTOR_GEAR_RATIO
//
//        steerConfigs = TalonFXConfiguration()
//
//        // Steer motor config
//        steerConfigs.Slot0.kP = PIDParameters.STEER_PID_AUTO.p
//        steerConfigs.Slot0.kI = PIDParameters.STEER_PID_AUTO.i
//        steerConfigs.Slot0.kD = PIDParameters.STEER_PID_AUTO.d
//        steerConfigs.Slot0.kV = 0.0
//        steerConfigs.ClosedLoopGeneral.ContinuousWrap = true
//
//        steerConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake
//        steerConfigs.MotorOutput.Inverted = SwerveParameters.Thresholds.STEER_MOTOR_INVERTED
//        steerConfigs.Feedback.FeedbackRemoteSensorID = canCoderID
//        steerConfigs.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.FusedCANcoder
//        steerConfigs.Feedback.RotorToSensorRatio = MotorParameters.STEER_MOTOR_GEAR_RATIO
//        steerConfigs.CurrentLimits.SupplyCurrentLimit = MotorParameters.STEER_SUPPLY_LIMIT
//        steerConfigs.CurrentLimits.SupplyCurrentLimitEnable = true
//
//        driveTorqueConfigs = TorqueCurrentConfigs()
//
//        val canCoderConfiguration = CANcoderConfiguration()
//
//        canCoderConfiguration.MagnetSensor.SensorDirection =
//            SensorDirectionValue.CounterClockwise_Positive
//        canCoderConfiguration.MagnetSensor.MagnetOffset =
//            SwerveParameters.Thresholds.ENCODER_OFFSET + canCoderDriveStraightSteerSetPoint
//        canCoderConfiguration.MagnetSensor.AbsoluteSensorDiscontinuityPoint = 1.0
//
//        driveMotor.configurator.apply(driveConfigs)
//        steerMotor.configurator.apply(steerConfigs)
//        canCoder.configurator.apply(canCoderConfiguration)
//
//        driveVelocity = driveMotor.velocity.valueAsDouble
//        drivePosition = driveMotor.position.valueAsDouble
//        steerVelocity = steerMotor.velocity.valueAsDouble
//        steerPosition = steerMotor.position.valueAsDouble
//
//        initializeLoggedNetworkPID()
//        initializeAlarms(driveId, steerId, canCoderID)
//    }
//
//    fun stop() {
//        steerMotor.stopMotor()
//        driveMotor.stopMotor()
//    }
//
//    fun setDrivePID(
//        pid: PIDController,
//        velocity: Double,
//    ) {
//        driveConfigs.Slot0.kP = pid.p
//        driveConfigs.Slot0.kI = pid.i
//        driveConfigs.Slot0.kD = pid.d
//        driveConfigs.Slot0.kV = velocity
//        driveMotor.configurator.apply(driveConfigs)
//    }
//
//    fun setSteerPID(
//        pid: PIDController,
//        velocity: Double,
//    ) {
//        steerConfigs.Slot0.kP = pid.p
//        steerConfigs.Slot0.kI = pid.i
//        steerConfigs.Slot0.kD = pid.d
//        steerConfigs.Slot0.kV = velocity
//        steerMotor.configurator.apply(steerConfigs)
//    }
//
//    fun applyTelePIDValues() {
//        driveConfigs.Slot0.kP = driveP.get()
//        driveConfigs.Slot0.kI = driveI.get()
//        driveConfigs.Slot0.kD = driveD.get()
//        driveConfigs.Slot0.kV = driveV.get()
//
//        steerConfigs.Slot0.kP = steerP.get()
//        steerConfigs.Slot0.kI = steerI.get()
//        steerConfigs.Slot0.kD = steerD.get()
//        steerConfigs.Slot0.kV = steerV.get()
//
//        driveMotor.configurator.apply(driveConfigs)
//        steerMotor.configurator.apply(steerConfigs)
//    }
//
//    fun setTelePID() {
//        setDrivePID(PIDParameters.DRIVE_PID_TELE, PIDParameters.DRIVE_PID_TELE.v)
//        setSteerPID(PIDParameters.STEER_PID_TELE, PIDParameters.STEER_PID_TELE.v)
//    }
//
//    fun setAutoPID() {
//        setDrivePID(PIDParameters.DRIVE_PID_AUTO, PIDParameters.DRIVE_PID_AUTO.v)
//    }
//
//    fun resetDrivePosition() {
//        driveMotor.setPosition(0.0)
//    }
//
//    fun initializeLoggedNetworkPID() {
//        driveP = LoggedNetworkNumber("/Tuning/Drive P", driveConfigs.Slot0.kP)
//        driveI = LoggedNetworkNumber("/Tuning/Drive I", driveConfigs.Slot0.kI)
//        driveD = LoggedNetworkNumber("/Tuning/Drive D", driveConfigs.Slot0.kD)
//        driveV = LoggedNetworkNumber("/Tuning/Drive V", driveConfigs.Slot0.kV)
//
//        steerP = LoggedNetworkNumber("/Tuning/Steer P", steerConfigs.Slot0.kP)
//        steerI = LoggedNetworkNumber("/Tuning/Steer I", steerConfigs.Slot0.kI)
//        steerD = LoggedNetworkNumber("/Tuning/Steer D", steerConfigs.Slot0.kD)
//        steerV = LoggedNetworkNumber("/Tuning/Steer V", steerConfigs.Slot0.kV)
//    }
//
//    fun initializeAlarms(
//        driveID: Int,
//        steerID: Int,
//        canCoderID: Int,
//    ) {
//        driveDisconnectedAlert =
//            Alert("DRIVE ALARM", "Disconnected drive motor $driveID.", Alert.Level.HIGH)
//        turnDisconnectedAlert =
//            Alert("Disconnected turn motor $steerID.", Alert.Level.HIGH)
//        canCoderDisconnectedAlert =
//            Alert("Disconnected CANCoder $canCoderID.", Alert.Level.HIGH)
//
//        driveDisconnectedAlert.set(!driveMotor.isConnected)
//        turnDisconnectedAlert.set(!steerMotor.isConnected)
//        canCoderDisconnectedAlert.set(!canCoder.isConnected)
//    }
//
//    fun updateTelePID() {
//        PIDParameters.DRIVE_PID_TELE.p = driveP.get()
//        PIDParameters.DRIVE_PID_TELE.i = driveI.get()
//        PIDParameters.DRIVE_PID_TELE.d = driveD.get()
//
//        PIDParameters.STEER_PID_TELE.p = steerP.get()
//        PIDParameters.STEER_PID_TELE.i = steerI.get()
//        PIDParameters.STEER_PID_TELE.d = steerD.get()
//
//        applyTelePIDValues()
//    }
// }
