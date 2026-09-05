package frc.robot.subsystems.drive

import frc.robot.utils.RobotParameters.SwerveParameters
import org.littletonrobotics.junction.Logger
import org.wpilib.driverstation.Alert
import org.wpilib.math.geometry.Rotation2d
import org.wpilib.math.kinematics.SwerveModulePosition
import org.wpilib.math.kinematics.SwerveModuleVelocity

class Module(
    private val io: ModuleIO,
    private val index: Int,
) {
    private val driveEnergyKey = "FullDrive/Drive/$index"
    private val turnEnergyKey = "FullDrive/Turn/$index"
    private val inputs = ModuleIOInputsAutoLogged()
    private val outputs = ModuleIO.ModuleIOOutputs()
    private val ffModel = SwerveParameters.PIDParameters.DRIVE_FF

    private val driveDisconnectedAlert =
        Alert(
            "Disconnected drive motor on module $index.",
            Alert.Level.HIGH,
        )

    private val turnDisconnectedAlert =
        Alert(
            "Disconnected turn motor on module $index.",
            Alert.Level.HIGH,
        )

    private val encoderDisconnectedAlert =
        Alert(
            "Disconnected encoder on module $index.",
            Alert.Level.HIGH,
        )

    fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Drive/Module$index", inputs)
    }

    fun periodicAfterScheduler() {
    }

    /** Runs the module with the specified setpoint velocity. */
    fun runSetpoint(state: SwerveModuleVelocity) {
        // optimize() and cosineScale() return NEW objects in 2027 — capture them!
        val optimized = state.optimize(getAngle())
        val scaled = optimized.cosineScale(inputs.turnPositionRads)

        // Apply setpoints
        val speedRadPerSec = scaled.velocity / (SwerveParameters.PhysicalParameters.WHEEL_DIAMETER / 2)

        outputs.mode = ModuleIO.ModuleIOOutputMode.DRIVE
        outputs.driveVelocityRadPerSec = speedRadPerSec
        outputs.driveFeedforward = ffModel.calculate(speedRadPerSec)
        outputs.turnRotation = scaled.angle
        outputs.turnNeutral =
            kotlin.math.abs(
                scaled.angle.minus(getAngle()).degrees,
            ) < SwerveParameters.Thresholds.TURN_DEADBAND_DEGREES
    }

    /** Runs the module with the specified output while controlling to zero degrees. */
    fun runCharacterization(output: Double) {
        outputs.mode = ModuleIO.ModuleIOOutputMode.CHARACTERIZE
        outputs.driveCharacterizationOutput = output
        outputs.turnRotation = Rotation2d.kZero
    }

    /** Disables all motor outputs in brake mode. */
    fun brake() {
        outputs.mode = ModuleIO.ModuleIOOutputMode.BRAKE
    }

    /** Disables all motor outputs in coast mode. */
    fun coast() {
        outputs.mode = ModuleIO.ModuleIOOutputMode.COAST
    }

    /** Returns whether the motors are connected. */
    fun isConnected(): Boolean = inputs.driveConnected && inputs.turnConnected

    /** Returns the current turn angle of the module. */
    fun getAngle(): Rotation2d = inputs.turnPositionRads

    /** Returns the current drive position of the module in meters. */
    fun getPositionMeters(): Double = inputs.drivePositionRads * SwerveParameters.PhysicalParameters.WHEEL_DIAMETER / 2

    /** Returns the current drive velocity of the module in meters per second. */
    fun getVelocityMetersPerSec(): Double = inputs.driveVelocityRadsPerSec * SwerveParameters.PhysicalParameters.WHEEL_DIAMETER / 2

    /** Returns the module position (turn angle and drive position). */
    fun getPosition(): SwerveModulePosition = SwerveModulePosition(getPositionMeters(), getAngle())

    /** Returns the module velocity (turn angle and drive velocity). */
    fun getState(): SwerveModuleVelocity = SwerveModuleVelocity(getVelocityMetersPerSec(), getAngle())

    /** Returns the module position in radians. */
    fun getWheelRadiusCharacterizationPosition(): Double = inputs.drivePositionRads

    /** Returns the module velocity in rotations/sec. */
    fun getFFCharacterizationVelocity(): Double = inputs.driveVelocityRadsPerSec
}
