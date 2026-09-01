package frc.robot.subsystems.pivot

import org.littletonrobotics.junction.AutoLog
import org.wpilib.units.Units.*

interface PivotIO {
    @AutoLog
    class PivotInputs {
        var leaderPosition = Radians.zero()
        var leaderVelocity = RPM.zero()
        var leaderAppliedVoltage = Volt.zero()
        var leaderCurrentDraw = Amp.zero()
        var leaderTemperature = Celsius.zero()
        var leaderBusVoltage = Volt.zero()
    }

    /**
     * Updates the inputs for the pivot subsystem.
     *
     * @param inputs Input data to be passed into pivot logic
     */
    fun updateInputs(inputs: PivotInputs)

    /**
     * sets the power that will be applied to the motor
     *
     * @param power the power applied to the motor between -1 and 1.
     */
    fun setPower(power: Double) {}

    /** Disables the motor  */
    fun disablePower() {}

    /**
     * sets the current limit for the intake
     *
     * @param currentLimit the maximum current limit
     */
    fun setCurrentLimit(currentLimit: Int) {}
}
