package com.parking;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 
 * @author Maad
 *
 */
public class TollPakingTestTool {

    private static Predicate<TestVehicle> isOfType(VehicleType type) {
        return c -> c.vehicleType == type;
    }

    static final Predicate<TestVehicle> isGASPredicate = isOfType(VehicleType.GASOLINE);
    static final Predicate<TestVehicle> isE50Predicate = isOfType(VehicleType.ELECTRIC_50KW);
    static final Predicate<TestVehicle> isE20Predicate = isOfType(VehicleType.ELECTRIC_20KW);
    static final Predicate<TestVehicle> isOther = isOfType(VehicleType.OTHER);

    

    static TestVehicle getGasVehicle() {
        return new TestVehicle(VehicleType.GASOLINE);
    }

    static TestVehicle getE20Vehicle() {
        return new TestVehicle(VehicleType.ELECTRIC_20KW);
    }

    static TestVehicle getE50Vehicle() {
        return new TestVehicle(VehicleType.ELECTRIC_50KW);
    }

    static TestVehicle getOtherVehicle() {
        return new TestVehicle(VehicleType.OTHER);
    }

    
    static List<TestVehicle> getGasVehicle(int numberOfVehicles) {
        return getMultipleTestVehicles(VehicleType.GASOLINE, numberOfVehicles);
    }
    
    static List<TestVehicle> getE20Vehicle(int numberOfVehicles) {
        return getMultipleTestVehicles(VehicleType.ELECTRIC_20KW, numberOfVehicles);
    }
    
    static List<TestVehicle> getE50Vehicle(int numberOfVehicles) {
        return getMultipleTestVehicles(VehicleType.ELECTRIC_50KW, numberOfVehicles);
    }
    
    static List<TestVehicle> getOtherVehicle(int numberOfVehicles) {
        return getMultipleTestVehicles(VehicleType.OTHER, numberOfVehicles);
    }

    private static List<TestVehicle> getMultipleTestVehicles(VehicleType carType, int numberOfVehicles) {
        return IntStream.range(0, numberOfVehicles).mapToObj(i -> new TestVehicle(carType)).collect(Collectors.toList());
    }


    static void assertPriceEquals(BigDecimal exceptedPrice, BigDecimal testedPrice) {
    	assertEquals(exceptedPrice.stripTrailingZeros(), testedPrice.stripTrailingZeros());
    }

    static BigDecimal money(int number) {
        return new BigDecimal(number);
    }
    
    static BigDecimal money(double number) {
        return new BigDecimal(number);
    }

    final static BigDecimal THREE = money(3);
    final static BigDecimal ONE = money(1);
    final static PricingPolicy<TestVehicle> THREE_HOURLY_AND_FIXED = PricingPolicy.HOURLY_AND_FIXED(THREE, ONE);


    
}
