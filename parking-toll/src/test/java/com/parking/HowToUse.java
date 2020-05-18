package com.parking;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HowToUse {

	//Specify different type of vehicles you want to manage
	public enum VehicleType {GAS, ELECTRICAL, OTHER}

	//Specify your main Vehicle class
	public class Vehicle {
		private final VehicleType vehicleType;
		Vehicle(VehicleType carType) {
			this.vehicleType = carType;
		}
		VehicleType getVehicleType() {
			return vehicleType;
		}
	}

	@Test
	public void testHowToUse() {
		//Build your parking
		Parking<Vehicle> parking = Parking.<Vehicle>builder()
				//Using Hourly pricing policy
				.setPricingPolicy(PricingPolicy.HOURLY(new BigDecimal(5)))
				// Add 10 slots for GAS only
				.addSlots(vehicle -> vehicle.getVehicleType() == VehicleType.GAS, 10)
				// Add 5 slots for ELECTRICAL only 
				.addSlots(vehicle -> vehicle.getVehicleType() == VehicleType.ELECTRICAL, 5).build();

		//Instantiate you vehicle using your preferred constructor
		Vehicle gasVehicle = new Vehicle(VehicleType.GAS);

		LocalDateTime departureDateTime = LocalDateTime.now();
		LocalDateTime arrivalDateTime = departureDateTime.minus(Duration.ofHours(2));

		//Register the vehicle in he parking
		Registration<Vehicle> registration = parking.register(gasVehicle, arrivalDateTime);

		assertTrue(registration.isSuccessful());

		//Check out from the parking
		Bill<Vehicle> bill = parking.checkOut(gasVehicle, departureDateTime);

		//Get the price
		bill.getPrice();

		assertEquals(new BigDecimal(10), bill.getPrice());

		//Hourly pricing policy
		PricingPolicy<Vehicle> hourly = PricingPolicy.HOURLY(new BigDecimal(3));
		//Custom pricing policy
		PricingPolicy<Vehicle> pricingPerVehicleType = slot ->{
			if(slot.getVehicle().getVehicleType() == VehicleType.ELECTRICAL) {
				//Free for this type of vehicles
				return new BigDecimal(0);
			}
			//Hourly for this type
			return hourly.computePrice(slot);
		};
	}

}