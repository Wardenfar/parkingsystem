package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Vérifie le calcul du tarif voiture correspondant à 1h")
    public void calculateFareCar() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR);
        assertEquals(price, Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Vérifie le calcul du tarif vélo correspondant à 1h")
    public void calculateFareBike() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE);
        assertEquals(price, Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Échec du calcul du tarif avec un type de véhicule invalide")
    public void calculateFareUnkownType() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        assertThrows(NullPointerException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, null));
    }

    @Test
    @DisplayName("Échec du calcul du tarif avec une date d'arrivée vide")
    public void calculateFareNullInTime() {
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(null, outTime, ParkingType.CAR));
    }

    @Test
    @DisplayName("Échec du calcul du tarif avec une date de départ vide")
    public void calculateFareNullOutTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, null, ParkingType.CAR));
    }

    @Test
    @DisplayName("Échec du calcul du tarif vélo avec une date d'arrivée invalide")
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("1900/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE));
    }

    @Test
    @DisplayName("Échec du calcul du tarif voiture avec une date d'arrivée invalide")
    public void calculateFareCarWithFutureInTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("1900/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR));
    }

    @Test
    @DisplayName("Vérifie le tarif vélo correspondant à moins d'une heure")
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE);
        assertEquals(price, 0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Vérifie le tarif voiture correspondant à moins d'une heure")
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR);
        assertEquals(price, 0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Vérifie le tarif voiture correspondant à plus d'un jour")
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR);
        assertEquals(price, 24 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @DisplayName("Vérifie le tarif vélo correspondant à plus d'un jour")
    public void calculateFareBikeWithMoreThanADayParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE);
        assertEquals(price, 24 * Fare.BIKE_RATE_PER_HOUR);
    }
}
