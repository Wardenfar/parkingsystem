package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inMs = ticket.getInTime().getTime();
        long outMs = ticket.getOutTime().getTime();
        long diffMs = outMs - inMs;
        float diffHours = diffMs / 1000f / 60f / 60f;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(diffHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(diffHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}