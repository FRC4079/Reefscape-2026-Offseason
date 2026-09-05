package frc.robot.subsystems.drive

import org.littletonrobotics.junction.AutoLog
import org.wpilib.math.geometry.Rotation2d

interface ModuleIO {
    @AutoLog
    open class ModuleIOInputs {
        @JvmField
        var data: ModuleIOData =
            ModuleIOData(
                driveConnected = false,
                drivePositionRad = 0.0,
                driveVelocityRadPerSec = 0.0,
                driveAppliedVolts = 0.0,
                driveSupplyCurrentAmps = 0.0,
                driveTorqueCurrentAmps = 0.0,
                turnConnected = false,
                turnEncoderConnected = false,
                turnAbsolutePosition = Rotation2d.kZero,
                turnPosition = Rotation2d.kZero,
                turnVelocityRadPerSec = 0.0,
                turnAppliedVolts = 0.0,
                turnSupplyCurrentAmps = 0.0,
                turnTorqueCurrentAmps = 0.0,
            )

        @JvmField
        var odometryDrivePositionsRad: DoubleArray = doubleArrayOf()

        @JvmField
        var odometryTurnPositions: Array<Rotation2d> = arrayOf()
    }


    data class ModuleIOData(
        val driveConnected: Boolean,
        val drivePositionRad: Double,
        val driveVelocityRadPerSec: Double,
        val driveAppliedVolts: Double,
        val driveSupplyCurrentAmps: Double,
        val driveTorqueCurrentAmps: Double,
        val turnConnected: Boolean,
        val turnEncoderConnected: Boolean,
        val turnAbsolutePosition: Rotation2d,
        val turnPosition: Rotation2d,
        val turnVelocityRadPerSec: Double,
        val turnAppliedVolts: Double,
        val turnSupplyCurrentAmps: Double,
        val turnTorqueCurrentAmps: Double,
    )

    fun updateInputs(inputs: ModuleIOInputs) {}

    fun runDriveOpenLoop(output: Double) {}

    fun runTurnOpenLoop(output: Double) {}

    fun runDriveVelocity(
        velocityRadPerSec: Double,
        feedforward: Double,
    ) {}

    fun runTurnPosition(rotation: Rotation2d) {}

    fun setDrivePID(
        kP: Double,
        kI: Double,
        kD: Double,
    ) {}

    fun setTurnPID(
        kP: Double,
        kI: Double,
        kD: Double,
    ) {}

    fun setBrakeMode(enabled: Boolean) {}
}
