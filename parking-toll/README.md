# Parking API
A Java Parking API ready to use  

## Install
install from source

mvn clean install
Jar: parking-toll/target/parking-toll-0.0.1-SNAPSHOT
Java doc: parking-toll/target/apidocs/index.html

## How to use 
Start by defining your Vehicle class you can add any details you want, for our example we will take the following:

```java
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
```

## Build your Parking

```java
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
        
```

## Define a pricing policy

```java
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
```

Please see the java doc for more examples and details.

## Limitation and future improvements

- This library does not have any currency management system.
- Finding a vehicle in the parking can be improved in the future by using a sytem that manages car registration number in order to identify a vehicle.
- Locating a vehicle in the parking can be very usefull, adding a system to manage floors and slots location in the parking can be a great improvement.




