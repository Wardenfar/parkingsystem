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
     * @param isRecurrentUser
     */
    public void calculateAndSetFare(Ticket ticket, boolean isRecurrentUser) {
        ticket.setPrice(calculateFare(ticket.getInTime(), ticket.getOutTime(), ticket.getParkingSpot().getParkingType(), isRecurrentUser));
    }

    /**
     * Calculate the fare price based on the inTime, outTime, the parkingType and if it's a recurrent user
     *
     * @param inTime
     * @param outTime
     * @param parkingType
     * @param isRecurrentUser
     * @return the calculated price
     */
    public double calculateFare(Date inTime, Date outTime, ParkingType parkingType, boolean isRecurrentUser) {
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

        // Subtract the Free Time
        double diffHoursWithFreeTime = diffHours - Fare.FREE_HOUR_THRESHOLD;

        // The difference is minimum 0.0
        if (diffHoursWithFreeTime < 0) {
            diffHoursWithFreeTime = 0.0;
        }

        // the price without the recurrent discount
        double priceWithoutDiscount;

        // The user was in the parking for more than the free threshold
        switch (parkingType) {
            case CAR:   // The price of CAR ticket
                priceWithoutDiscount = diffHoursWithFreeTime * Fare.CAR_RATE_PER_HOUR;
                break;
            case BIKE:  // The price of BIKE ticket
                priceWithoutDiscount = diffHoursWithFreeTime * Fare.BIKE_RATE_PER_HOUR;
                break;
            default:    // Error : Unkown Parking Type
                throw new IllegalArgumentException("Unkown Parking Type");
        }


        // The final price
        double price;

        // If it's a recurrent user : apply a discount
        if (isRecurrentUser) {
            price = priceWithoutDiscount * (1 - Fare.RECURRENT_DISCOUNT_PERCENT);
        } else {
            price = priceWithoutDiscount;
        }

        // return the rounded price with two decimals
        return round(price);
    }

    public static double round(double price) {
        return Math.round(price * 100.0) / 100.0;
    }
}
