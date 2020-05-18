package com.parking;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 
 * @author Maad
 *
 */
public class ParkingTest extends TollPakingTestTool {

    private List<TestVehicle> vehicles;
    
    private LocalDateTime now,threHoursInThePast, oneHourInThePast;
    
    private TestVehicle gasVehicle,e20Vehicle, e50Vehicle,otherVehicle;
    
    private Parking<TestVehicle> parking;
    

    @Before
    public void init() {
        
    	/**
    	 * Capacity: 
    	 * 10 for E20
    	 * 5 for E50
    	 * 30 for Gas
    	 * Total capacity: 45
    	 */
    	parking = Parking.<TestVehicle>builder().setPricingPolicy(THREE_HOURLY_AND_FIXED)
    			//slots that accept e20 only
                .addSlots(isE20Predicate, 10)
                //slots that accept e50 only
                .addSlots(isE50Predicate, 5)
                //slots that accept gas only
                .addSlots(isGASPredicate,30)
                .build();
        
        vehicles = getE20Vehicle(5);  
        vehicles.addAll(getE50Vehicle(5));  
        vehicles.addAll(getGasVehicle(15));  
        
        gasVehicle = getGasVehicle();
        e20Vehicle = getE20Vehicle();
        e50Vehicle = getE50Vehicle();
        otherVehicle = getOtherVehicle();
        
    	now = LocalDateTime.now();
    	oneHourInThePast = now.minus(Duration.ofHours(1));
    	threHoursInThePast = now.minus(Duration.ofHours(3));

    }

    @Test
    public void testRegistration() {
        Registration<TestVehicle> registerResult = parking.register(e20Vehicle, now);
        assertTrue(registerResult.isSuccessful());
        Slot<TestVehicle> slot = registerResult.getSlot();
        assertEquals(e20Vehicle, slot.getVehicle());
        assertEquals(now, slot.getArrivalDateTime());
    }

    @Test
    public void testResitrationFailed() {
        // register all available slots
        getE20Vehicle(10).forEach(parking::register);
        
        assertEquals(0, parking.getAvailableCapacity(e20Vehicle));
        
        //No slot is available
        assertFalse(parking.register(e20Vehicle).isSuccessful()); 
        
        //type of vehicle not accepted by the parking
        assertFalse(parking.register(otherVehicle).isSuccessful());
    }

    @Test
    public void testCheckOut() {
    	
    	Registration<TestVehicle> registration = parking.register(gasVehicle, threHoursInThePast);
        assertTrue(registration.isSuccessful());
        Bill<TestVehicle> slotSnapShot = parking.checkOut(gasVehicle, oneHourInThePast);
        Slot<TestVehicle> slotSnapshot = slotSnapShot.getSlot();
        assertEquals(threHoursInThePast, slotSnapshot.getArrivalDateTime());
        assertEquals(oneHourInThePast, slotSnapshot.getDepartureDateTime());
        assertEquals(Duration.ofHours(2), slotSnapshot.getStayDuration());
        assertTrue(slotSnapshot.getAllocationStrategy().test(gasVehicle));
        
        // 2 hours * 3 + 1(for fixed rate) = 7
        assertPriceEquals(new BigDecimal(7), slotSnapShot.getPrice());
    }

    /**
     * Testing total capacity of the parking
     * which is not impacted if there is any vehicles in or not
     */
    @Test
    public void testCapacity() {
    	//capacity total is 45
    	assertEquals(45, parking.getCapacity());
    	
        assertEquals(30, parking.getCapacity(gasVehicle));
        assertEquals(5, parking.getCapacity(e50Vehicle));
        assertEquals(10, parking.getCapacity(e20Vehicle));

        
        // Registering 20 vehicles 
        // 5 e20
        // 5 e50
        // 15 Gas
        vehicles.forEach(parking::register);
        
        //we check that the capacity is unchanged
        assertEquals(30, parking.getCapacity(gasVehicle));
        assertEquals(5, parking.getCapacity(e50Vehicle));
        assertEquals(10, parking.getCapacity(e20Vehicle));
        
    }


    /*
     * Test available capacity: only free slots
     */
    @Test
    public void testGetAvailableCapacity() {
    	
    	assertEquals(45, parking.getAvailableCapacity());

        assertEquals(10, parking.getAvailableCapacity(e20Vehicle));
        assertEquals(5, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(30, parking.getAvailableCapacity(gasVehicle));
        
        //Registering 20 vehicles
        // 5 e20
        // 5 e50
        // 15 Gas
        vehicles.forEach(parking::register);
        assertEquals(5, parking.getAvailableCapacity(e20Vehicle));
        assertEquals(0, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(15, parking.getAvailableCapacity(gasVehicle));
        
        //45-25
        assertEquals(20, parking.getAvailableCapacity());

        
        //Freeing all the parking
        vehicles.forEach(parking::checkOut);
        assertEquals(10, parking.getAvailableCapacity(e20Vehicle));
        assertEquals(5, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(30, parking.getAvailableCapacity(gasVehicle));
        
    	assertEquals(45, parking.getAvailableCapacity());

    }
    
    
    @Test
    public void testParking() {
        parking.register(gasVehicle);
        assertEquals(29, parking.getAvailableCapacity(gasVehicle));
        assertEquals(5, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(10, parking.getAvailableCapacity(e20Vehicle));
        assertEquals(44, parking.getAvailableCapacity());

        
        parking.register(e50Vehicle);
        assertEquals(4, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(29, parking.getAvailableCapacity(gasVehicle));
        assertEquals(10, parking.getAvailableCapacity(e20Vehicle));
        assertEquals(43, parking.getAvailableCapacity());


        assertEquals(e50Vehicle, parking.checkOut(e50Vehicle).getSlot().getVehicle());
        assertEquals(5, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(5, parking.getAvailableCapacity(e50Vehicle));
        assertEquals(10, parking.getAvailableCapacity(e20Vehicle));
        assertEquals(44, parking.getAvailableCapacity());

        assertEquals(gasVehicle, parking.checkOut(gasVehicle).getSlot().getVehicle());
        assertEquals(30, parking.getAvailableCapacity(gasVehicle));
        assertEquals(45, parking.getAvailableCapacity());

    }


    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testInvalidDepartureDate() {
        parking.register(gasVehicle, now);
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("departureDateTime should be after arrivalDateTime!");
        parking.checkOut(gasVehicle, oneHourInThePast);
    }

}
