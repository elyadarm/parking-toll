package com.parking;

public enum VehicleType {
	ELECTRIC_20KW,
    ELECTRIC_50KW,
	GASOLINE,
    OTHER;
    
	public boolean isElectric() {
		return this == VehicleType.ELECTRIC_20KW || this == VehicleType.ELECTRIC_20KW;
	}
	
}
