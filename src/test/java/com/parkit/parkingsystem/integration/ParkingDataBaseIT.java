package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.TestUtils;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static com.parkit.parkingsystem.service.FareCalculatorService.round;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        inputReaderUtil = mock(InputReaderUtil.class);
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() {
        // === Given
        ParkingService parkingService = helperSetupParkingService(ParkingType.CAR, "ABCDEF");
        Date inTime = TestUtils.parseTime("2020/01/01 15:00");

        // === When
        setCurrentTime(parkingService, inTime);
        parkingService.processIncomingVehicle();

        // === Then

        // Fetch from DB the created ticket and his parking spot
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        // Test the ticket
        assertNotNull(ticket);
        assertEquals(ticket.getVehicleRegNumber(), "ABCDEF");
        assertEquals(ticket.getPrice(), 0);
        assertEquals(ticket.getInTime().getTime(), inTime.getTime());
        assertNull(ticket.getOutTime());

        // Test the parking spot
        assertNotNull(parkingSpot);
        assertFalse(parkingSpot.isAvailable());
        assertEquals(parkingSpot.getParkingType(), ParkingType.CAR);
    }

    @Test
    public void testParkingABike() {
        // === Given
        ParkingService parkingService = helperSetupParkingService(ParkingType.BIKE, "ABCDEFBIKE");

        // === When
        parkingService.processIncomingVehicle();

        // === Then

        // Fetch from DB the created ticket and his parking spot
        Ticket ticket = ticketDAO.getTicket("ABCDEFBIKE");
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        // Test the ticket
        assertNotNull(ticket);
        assertEquals(ticket.getVehicleRegNumber(), "ABCDEFBIKE");
        assertEquals(ticket.getPrice(), 0);
        assertNull(ticket.getOutTime());

        // Test the parking spot
        assertNotNull(parkingSpot);
        assertFalse(parkingSpot.isAvailable());
        assertEquals(parkingSpot.getParkingType(), ParkingType.BIKE);
    }


    @Test
    public void testParkingLotExitCar() {
        // === Given
        ParkingService parkingService = helperSetupParkingService(ParkingType.CAR, "ABCDEF");
        Date inTime = TestUtils.parseTime("2020/01/01 15:00");
        Date outTime = TestUtils.parseTime("2020/01/01 18:30");

        // === When
        setCurrentTime(parkingService, inTime);
        parkingService.processIncomingVehicle();

        setCurrentTime(parkingService, outTime);
        parkingService.processExitingVehicle();

        // === Then

        // Fetch from DB the updated ticket and his parking spot
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        // Test the ticket
        assertNotNull(ticket);
        assertEquals(ticket.getVehicleRegNumber(), "ABCDEF");
        assertEquals(ticket.getInTime().getTime(), inTime.getTime());
        assertEquals(ticket.getOutTime().getTime(), outTime.getTime());
        assertEquals(round((3.5 - Fare.FREE_HOUR_THRESHOLD) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());

        // Test the parking spot
        assertNotNull(parkingSpot);
        assertTrue(parkingSpot.isAvailable());
        assertEquals(parkingSpot.getParkingType(), ParkingType.CAR);
    }

    @Test
    public void testParkingLotExitBike() {
        // === Given
        ParkingService parkingService = helperSetupParkingService(ParkingType.BIKE, "ABCDEF");
        Date inTime = TestUtils.parseTime("2020/01/01 14:00");
        Date outTime = TestUtils.parseTime("2020/01/01 18:30");

        // === When
        setCurrentTime(parkingService, inTime);
        parkingService.processIncomingVehicle();

        setCurrentTime(parkingService, outTime);
        parkingService.processExitingVehicle();

        // === Then

        // Fetch from DB the updated ticket and his parking spot
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        // Test the ticket
        assertNotNull(ticket);
        assertEquals(ticket.getVehicleRegNumber(), "ABCDEF");

        assertEquals(ticket.getInTime().getTime(), inTime.getTime());
        assertEquals(ticket.getOutTime().getTime(), outTime.getTime());

        assertEquals(round((4.5 - Fare.FREE_HOUR_THRESHOLD) * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());

        // Test the parking spot
        assertNotNull(parkingSpot);
        assertTrue(parkingSpot.isAvailable());
        assertEquals(parkingSpot.getParkingType(), ParkingType.BIKE);
    }


    /**
     * Helper Function that create a parkingService and mock inputs of {@link InputReaderUtil}
     *
     * @param parkingType
     * @param vehicleRegistrationNumber
     * @return the created ParkingService
     */
    private ParkingService helperSetupParkingService(ParkingType parkingType, String vehicleRegistrationNumber) {
        try {
            int selection = 0;
            switch (parkingType) {
                case CAR: {
                    selection = 1;
                    break;
                }
                case BIKE: {
                    selection = 2;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal Parking Type");
                }
            }

            when(inputReaderUtil.readSelection()).thenReturn(selection);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegistrationNumber);
        } catch (Exception e) {
            System.err.println("Error during the setup of inputs");
            e.printStackTrace();
        }

        return spy(new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO));
    }

    /**
     * Helper Function that mock {@link ParkingService#getCurrentTime()} and set the return value from the parameter
     *
     * @param parkingService the affected parkingService
     * @param time           desired current time
     */
    public void setCurrentTime(ParkingService parkingService, Date time) {
        try {
            when(parkingService.getCurrentTime()).thenReturn(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
