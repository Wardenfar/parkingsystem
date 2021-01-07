package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    public void calculateAndSetFare(Ticket ticket) {
        ticket.setPrice(calculateFare(ticket.getInTime(), ticket.getOutTime(), ticket.getParkingSpot().getParkingType()));
    }

    public double calculateFare(Date inTime, Date outTime, ParkingType parkingType) {
        if (inTime == null) {
            throw new IllegalArgumentException("In time provided is null");
        }
        if ((outTime == null) || (outTime.before(inTime))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + outTime);
        }

        long inMs = inTime.getTime();
        long outMs = outTime.getTime();
        long diffMs = outMs - inMs;
        float diffHours = diffMs / 1000f / 60f / 60f;

        switch (parkingType) {
            case CAR: {
                return diffHours * Fare.CAR_RATE_PER_HOUR;
            }
            case BIKE: {
                return diffHours * Fare.BIKE_RATE_PER_HOUR;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}