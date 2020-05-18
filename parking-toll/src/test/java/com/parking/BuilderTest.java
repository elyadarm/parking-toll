package com.parking;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.parking.exception.ParkingException;
import com.parking.exception.PrincingPolicyException;

/**
 * 
 * @author Maad
 *
 */
public class BuilderTest extends TollPakingTestTool {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();


	@Test
	public void addSlotTest() {
		Parking<TestVehicle> parking = Parking.<TestVehicle>builder().setPricingPolicy(PricingPolicy.HOURLY(money(5)))
				.addSlot(isGASPredicate)
				.addSlot(isE20Predicate)
				.addSlot(isE50Predicate)
				.addSlot(isOther)
				.addSlot(isE20Predicate.or(isE50Predicate))
				.addSlot(isE20Predicate.or(isE50Predicate).or(isGASPredicate))
				.build();

		assertTrue(parking.validate());
		Assert.assertEquals(parking.getAvailableCapacity(), 6);
		Assert.assertEquals(parking.getCapacity(), 6);
	}

	@Test
	public void addSlots() {
		Builder<TestVehicle> builder = Parking.builder();
		Parking<TestVehicle> parking = builder.setPricingPolicy(PricingPolicy.HOURLY(THREE))
				.addSlots(isE50Predicate, 10)
				.addSlots(isGASPredicate, 40)
				.addSlots(isE20Predicate, 20)
				.build();
		assertTrue(parking.validate());
		Assert.assertEquals(parking.getAvailableCapacity(), 70);
		Assert.assertEquals(parking.getCapacity(), 70);	
	}

	@Test
	public void pricingPolicyNotFound() {
		expectedException.expect(PrincingPolicyException.class);
		expectedException.expectMessage("Pricing pollicy is required to create a parking!");
		Parking.<TestVehicle>builder().addSlot(isE20Predicate).build();
	}

	@Test
	public void noSlotFound() {
		expectedException.expect(ParkingException.class);
		expectedException.expectMessage("The paking should contain at least one slot!");
		Parking.<TestVehicle>builder().setPricingPolicy(PricingPolicy.HOURLY(money(7))).build();
	}


}
