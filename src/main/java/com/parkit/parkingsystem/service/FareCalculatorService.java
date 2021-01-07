package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    /**
     * Calculate the price for this ticket and update it
     *
     * @param ticket
     */
    public void calculateAndSetFare(Ticket ticket) {
        ticket.setPrice(calculateFare(ticket.getInTime(), ticket.getOutTime(), ticket.getParkingSpot().getParkingType()));
    }

    /**
     * Calculate the fare price based on the inTime, outTime and the parkingType
     * @param inTime
     * @param outTime
     * @param parkingType
     * @return the calculated price
     */
    public double calculateFare(Date inTime, Date outTime, ParkingType parkingType) {
        if (inTime == null) {
            throw new IllegalArgumentException("In time provided is null");
        }
        if (outTime == null) {
            throw new IllegalArgumentException("Out time provided is null");
        }
        if (outTime.before(inTime)) { // The outTime is before the inTime : throw an error
            throw new IllegalArgumentException("Out time provided is incorrect:" + outTime);
        }

        // inMs : Milliseconds since EPOCH
        long inMs = inTime.getTime();
        // outMs : Milliseconds since EPOCH
        long outMs = outTime.getTime();

        // Difference between milliseconds
        long diffMs = outMs - inMs;
        // Convert milliseconds in hours
        float diffHours = diffMs / 1000f / 60f / 60f;

        // The user was in the parking for less or equal than the free threshold
        if(diffHours <= Fare.FREE_HOUR_THRESHOLD){
            return 0.0;
        }

        // The user was in the parking for more than the free threshold
        switch (parkingType) {
            case CAR: { // The price of CAR ticket
                return diffHours * Fare.CAR_RATE_PER_HOUR;
            }
            case BIKE: { // The price of BIKE ticket
                return diffHours * Fare.BIKE_RATE_PER_HOUR;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}