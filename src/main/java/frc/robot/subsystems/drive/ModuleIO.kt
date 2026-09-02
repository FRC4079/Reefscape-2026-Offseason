package frc.robot.subsystems.drive

import org.littletonrobotics.junction.AutoLog
import org.wpilib.math.geometry.Rotation2d
import org.wpilib.units.Units.Celsius

interface ModuleIO {
    @AutoLog
    class ModuleIOInputs {
        @JvmField var driveConnected = false

        @JvmField var drivePositionRads = 0.0

        @JvmField var driveVelocityRadsPerSec = 0.0

        @JvmField var driveAppliedVolts = 0.0

        @JvmField var driveSupplyCurrentAmps = 0.0

        @JvmField var driveTorqueCurrentAmps = 0.0

        @JvmField var driveTempCelsius = Celsius.zero()

        @JvmField var turnConnected = false

        @JvmField var encoderConnected = false

        @JvmField var turnAbsolutePositionRads = Rotation2d.kZero

        @JvmField var turnPositionRads = Rotation2d.kZero

        @JvmField var turnVelocityRadsPerSec = 0.0

        @JvmField var turnAppliedVolts = 0.0

        @JvmField var turnSupplyCurrentAmps = 0.0

        @JvmField var turnTorqueCurrentAmps = 0.0

        @JvmField var turnTempCelsius = Celsius.zero()
    }

    enum class ModuleIOOutputMode {
        COAST,
        BRAKE,
        DRIVE,
        CHARACTERIZE,
    }

    class ModuleIOOutputs {
        var mode = ModuleIOOutputMode.COAST
        var driveVelocityRadPerSec = 0.0
        var driveFeedforward = 0.0
        var driveCharacterizationOutput = 0.0
        var turnRotation = Rotation2d.kZero
        var turnNeutral = false

        var driveSupplyCurrentLimit = 0.0
    }

    fun updateInputs(inputs: ModuleIOInputs) {}

    fun applyOutputs(outputs: ModuleIOOutputs) {}
}
