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

import static com.parkit.parkingsystem.service.FareCalculatorService.round;
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

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, false);
        assertEquals(price, round((1 - Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("Vérifie le calcul du tarif vélo correspondant à 1h")
    public void calculateFareBike() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, false);
        assertEquals(price, round((1 - Fare.FREE_HOUR_THRESHOLD) * Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("Échec du calcul du tarif avec un type de véhicule invalide")
    public void calculateFareUnkownType() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        assertThrows(NullPointerException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, null, false));
    }

    @Test
    @DisplayName("Échec du calcul du tarif avec une date d'arrivée vide")
    public void calculateFareNullInTime() {
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(null, outTime, ParkingType.CAR, false));
    }

    @Test
    @DisplayName("Échec du calcul du tarif avec une date de départ vide")
    public void calculateFareNullOutTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, null, ParkingType.CAR, false));
    }

    @Test
    @DisplayName("Échec du calcul du tarif vélo avec une date d'arrivée invalide")
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("1900/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, false));
    }

    @Test
    @DisplayName("Échec du calcul du tarif voiture avec une date d'arrivée invalide")
    public void calculateFareCarWithFutureInTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("1900/01/02 09:00");

        assertThrows(IllegalArgumentException.class, () ->
                fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, false));
    }

    @Test
    @DisplayName("Vérifie le tarif vélo correspondant à moins d'une heure")
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, false);
        assertEquals(price, round(0.75 * Fare.BIKE_RATE_PER_HOUR - Fare.FREE_HOUR_THRESHOLD));
    }

    @Test
    @DisplayName("Vérifie le tarif voiture correspondant à moins d'une heure")
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, false);
        assertEquals(price, round((0.75 - Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("Vérifie le tarif voiture correspondant à plus d'un jour")
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, false);
        assertEquals(price, round((24 - Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @DisplayName("Vérifie le tarif vélo correspondant à plus d'un jour")
    public void calculateFareBikeWithMoreThanADayParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, false);
        assertEquals(price, round(24 * Fare.BIKE_RATE_PER_HOUR - Fare.FREE_HOUR_THRESHOLD));
    }

    @Test
    @DisplayName("Vérifie le tarif gratuit correspondant à 30 min")
    public void calculateFreeFare30MinParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/01 08:00");
        Date outTime = TestUtils.parseTime("2020/01/01 08:30");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, false);
        assertEquals(price, round(0));
    }

    @Test
    @DisplayName("Vérifie le tarif payant correspondant à 31 min")
    public void calculateFare31MinParkingTime() {
        Date inTime = TestUtils.parseTime("2020/01/01 08:00");
        Date outTime = TestUtils.parseTime("2020/01/01 08:31");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, false);
        assertTrue(price > 0);
    }

    @Test
    @DisplayName("Vérifie le calcul du tarif voiture correspondant à 1h avec un utilisateur récurrent")
    public void calculateFareCarRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, true);
        assertEquals(price, round((1 * Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR * (1 - Fare.RECURRENT_DISCOUNT_PERCENT)));
    }

    @Test
    @DisplayName("Vérifie le calcul du tarif vélo correspondant à 1h avec un utilisateur récurrent")
    public void calculateFareBikeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 09:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, true);
        assertEquals(price, round((Fare.BIKE_RATE_PER_HOUR - Fare.FREE_HOUR_THRESHOLD) * (1 - Fare.RECURRENT_DISCOUNT_PERCENT)));
    }

    @Test
    @DisplayName("Vérifie le tarif vélo correspondant à moins d'une heure avec un utilisateur récurrent")
    public void calculateFareBikeWithLessThanOneHourParkingTimeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, true);
        assertEquals(price, round((0.75 - Fare.FREE_HOUR_THRESHOLD) * Fare.BIKE_RATE_PER_HOUR * (1 - Fare.RECURRENT_DISCOUNT_PERCENT)));
    }

    @Test
    @DisplayName("Vérifie le tarif voiture correspondant à moins d'une heure avec un utilisateur récurrent")
    public void calculateFareCarWithLessThanOneHourParkingTimeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/02 08:45");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, true);
        assertEquals(price, round((0.75 - Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR * (1 - Fare.RECURRENT_DISCOUNT_PERCENT)));
    }

    @Test
    @DisplayName("Vérifie le tarif voiture correspondant à plus d'un jour avec un utilisateur récurrent")
    public void calculateFareCarWithMoreThanADayParkingTimeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.CAR, true);
        assertEquals(price, round((24 - Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR * (1 - Fare.RECURRENT_DISCOUNT_PERCENT)));
    }

    @Test
    @DisplayName("Vérifie le tarif vélo correspondant à plus d'un jour avec un utilisateur récurrent")
    public void calculateFareBikeWithMoreThanADayParkingTimeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/02 08:00");
        Date outTime = TestUtils.parseTime("2020/01/03 08:00");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, true);
        assertEquals(price, round((24 - Fare.FREE_HOUR_THRESHOLD) * Fare.BIKE_RATE_PER_HOUR * (1 - Fare.RECURRENT_DISCOUNT_PERCENT)));
    }

    @Test
    @DisplayName("Vérifie le tarif gratuit correspondant à 30 min avec un utilisateur récurrent")
    public void calculateFreeFare30MinParkingTimeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/01 08:00");
        Date outTime = TestUtils.parseTime("2020/01/01 08:30");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, true);
        assertEquals(price, 0);
    }

    @Test
    @DisplayName("Vérifie le tarif payant correspondant à 31 min avec un utilisateur récurrent")
    public void calculateFare31MinParkingTimeRecurrentUser() {
        Date inTime = TestUtils.parseTime("2020/01/01 08:00");
        Date outTime = TestUtils.parseTime("2020/01/01 08:31");

        double price = fareCalculatorService.calculateFare(inTime, outTime, ParkingType.BIKE, true);
        assertTrue(price > 0); // 31 min / 1 Hour
    }
}
