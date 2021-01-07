package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR);
        assertEquals(price, Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE);
        assertEquals(price, Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        assertThrows(NullPointerException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, null));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("1900/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE));
    }

    @Test
    public void calculateFareCarWithFutureInTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("1900/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE);
        assertEquals(price, 0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR);
        assertEquals(price, 0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR);
        assertEquals(price, 24 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBikeWithMoreThanADayParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE);
        assertEquals(price, 24 * Fare.BIKE_RATE_PER_HOUR);
    }
}
