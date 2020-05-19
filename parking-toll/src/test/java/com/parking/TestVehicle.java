package com.parking;

/**
 * 
 * @author Maad
 *
 */
public class TestVehicle {

    private final int id;
    final VehicleType vehicleType;
    private static int counter = 0;

    TestVehicle(VehicleType vehicleType) {
        this.id = counter++;
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", vehicleType, id);
    }

	public int getId() {
		return id;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}
    
}
