package com.parking;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class SlotTest extends TollPakingTestTool {

    
	/** 
	 * Just a random allocation strategy for test needs
	 */
	private Predicate<TestVehicle> someAllocationStrategy = vehicle -> true;
	
    @Rule
    public ExpectedException expectedException = ExpectedException.none();;
	
	/**
	 * Test the creation of a slot
	 */
	@Test
    public void slotCreationSccess() {
        Slot<TestVehicle> slot = new Slot<>(0L, someAllocationStrategy);
        assertTrue(slot.isFree());
        assertNotNull(slot.getAllocationStrategy());
        assertNull(slot.getArrivalDateTime());
        assertNull(slot.getDepartureDateTime());
    }
	
	@Test
    public void slotCreationFailure() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("allocationStrategy cannot be null !");
        new Slot<>(0L, null);
    }

	
	/**
	 * Test allocating a vehicle with success 
	 */
    @Test
    public void allocateSuccess() {
    	 TestVehicle vehicle = getGasVehicle();
         Slot<TestVehicle> slot = new Slot<>(0L, someAllocationStrategy);
         Slot<TestVehicle> s = slot.allocate(vehicle, LocalDateTime.now());
         assertFalse(s.isFree());
         assertEquals(vehicle, s.getVehicle());
         assertNotNull(s.getArrivalDateTime());
    }
    
    /**
     * Failed allocating
     */
    @Test
    public void allocateFailure() {
        TestVehicle vehicle = getGasVehicle();
        Slot<TestVehicle> slot = new Slot<>(0L, someAllocationStrategy);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("arrivalDateTime cannot be null !");
        slot.allocate(vehicle, null);
    }
    
    

    @Test
    public void computeDuration() {
        Slot<TestVehicle> slot = new Slot<>(0L, someAllocationStrategy);
        assertEquals(Duration.ZERO, slot.getStayDuration());
        
        //some tweaking of the code for testing purposes
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeHoursInThePast = now.minus(Duration.ofHours(3));
       
        
        slot.allocate(getGasVehicle(), threeHoursInThePast);
        assertNotEquals(Duration.ZERO, slot.getStayDuration());
        
        
        LocalDateTime oneHourInThePast = now.minus(Duration.ofHours(1));
        Slot<TestVehicle> snapshotSlot = slot.free(oneHourInThePast);
        assertNotEquals(Duration.ZERO, snapshotSlot.getStayDuration());
        assertEquals(Duration.ofHours(2), snapshotSlot.getStayDuration());
        
    }

}
