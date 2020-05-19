package com.parking;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.Assert.assertEquals;

/**
 * 
 * @author Maad
 *
 */
public class PricingPolicyTest extends TollPakingTestTool {


	
	private final BigDecimal THREE_RATE = new BigDecimal(3);
	@Test
	public void Hourly() {
		assertPriceIsEqual(new BigDecimal(1.50),PricingPolicy.HOURLY(THREE_RATE), 30);
		assertPriceIsEqual(THREE_RATE,PricingPolicy.HOURLY(THREE_RATE), 60);
		assertPriceIsEqual(new BigDecimal(4.50),PricingPolicy.HOURLY(THREE_RATE), 90);

	}

	@Test
	public void Fixed() {
		BigDecimal fixed5Price = new BigDecimal(5);
		PricingPolicy<TestVehicle> fixedPolicy= PricingPolicy.FIXED(fixed5Price);
		assertPriceIsEqual(fixed5Price, fixedPolicy, 30);
		assertPriceIsEqual(fixed5Price, fixedPolicy, 60);
		assertPriceIsEqual(fixed5Price, fixedPolicy, 119);
	}

	@Test
	public void SUM() {
		BigDecimal fixed2Price = new BigDecimal(2);
		PricingPolicy<TestVehicle> and = PricingPolicy.HOURLY_AND_FIXED(THREE_RATE, fixed2Price);
		assertPriceIsEqual(new BigDecimal(3.5), and, 30);
		assertPriceIsEqual(new BigDecimal(5), and, 60);
	}


	@Test
	public void setCustomPricingPolicy() {
		// Use custom pricing policy: free for some kind of vehicles 
		PricingPolicy<TestVehicle> freeForElectric = slot -> isE20Predicate.or(isE50Predicate).test(slot.getVehicle()) ? money(0) : THREE_HOURLY_AND_FIXED.computePrice(slot);

		Parking<TestVehicle> parking = Parking.<TestVehicle>builder()
				.setPricingPolicy(freeForElectric)
				.addSlot(isE20Predicate)
				.addSlot(isGASPredicate)
				.build();

		LocalDateTime arrivalDateTime = LocalDateTime.now();
		LocalDateTime departureDateTime = arrivalDateTime.plus(Duration.ofMinutes(60));

		TestVehicle electricVehicle = getE20Vehicle();
		parking.register(electricVehicle, arrivalDateTime);
		Bill<TestVehicle> bill = parking.checkOut(electricVehicle, departureDateTime);
		//free for electric
		Assert.assertEquals(new BigDecimal(0), bill.getPrice());

		TestVehicle gasVehicle = getGasVehicle();
		parking.register(gasVehicle, arrivalDateTime);
		Bill<TestVehicle> billG = parking.checkOut(gasVehicle, departureDateTime);
		//not free for Gas
		Assert.assertEquals(new BigDecimal(4).stripTrailingZeros(), billG.getPrice().stripTrailingZeros());

	}


	/**
	 * Helper for testing pricing policies
	 * @param expectedPrice
	 * @param pricingPolicy
	 * @param duration
	 */
	private static void assertPriceIsEqual(BigDecimal expectedPrice, PricingPolicy<TestVehicle> pricingPolicy, int duration) {
		LocalDateTime arrival = LocalDateTime.now();
		LocalDateTime departure = arrival.plus(Duration.ofMinutes(duration));
		Slot<TestVehicle> slot = new Slot<TestVehicle>(0L, t -> true, getE20Vehicle(), arrival, departure);
		assertEquals(expectedPrice.stripTrailingZeros(),pricingPolicy.computePrice(slot).stripTrailingZeros());
	}
}
