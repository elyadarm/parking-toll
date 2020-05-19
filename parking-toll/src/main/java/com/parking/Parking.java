package com.parking;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.parking.exception.ParkingException;
import com.parking.exception.PrincingPolicyException;

/**
 * Parking class compatible with any Vehicle class you are using 
 * Use {@link Parking#register(Object)} for vehicle registration in the parking
 * Use {@link Parking#checkOut(Object)} to leave the parking and calculate the price
 * Use {@link Parking#builder()} to create the parking slots using your slot allocation strategy and your pricing policy.
 * @param <T> the vehicle class you want to use
 * @author Maad
 */
public class Parking<T> {

	
	/**
	 *List of all parking slots
	 *This list should contains at least one element 
	 */
	private final List<Slot<T>> slots = new ArrayList<>();
	
    /**
     * Pricing policy used during {@link Parking#checkOut(Object)}
     * Required for creating a parking
     */
    private PricingPolicy<T> pricingPolicy;

    /**
     * Constructor used by the Builder
     */
    Parking() {
    }

    /**
     * Return new {@link Builder} ready for use to create your parking
     * @param <T> your vehicle class
     * @return the builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }
    
    /**
     * Capacity of the parking, total number of slots
     * @return the capacity of the parking.
     */
    public long getCapacity() {
        return slots.size();
    }
    
    /**
     * Capacity of the parking for this kind of vehicles.
     * This is only the capacity, not sure they are all free
     * @param vehicle vehicle class
     * @return number of slots for this vehicle.
     */
    public long getCapacity(T vehicle) {
        return slots.stream().filter(slot -> slot.test(vehicle)).count();
    }

    /**
     * Get Current Available slots of the parking
     * @return {@link Slot} stream on free slots.
     */
    private Stream<Slot<T>> getAvailable() {
        return slots.stream().filter(Slot::isFree);
    }
    
    /**
     * Return a stream containing all available slots for this vehicle using allocation policy strategy
     * @param vehicle vehicle class
     * @return stream on available slots for the vehicle
     */
    private Stream<Slot<T>> getAvailable(T vehicle) {
        return getAvailable().filter(slot -> slot.test(vehicle));
    }
    
    /**
     * Available capacity of the parking for the vehicle (number of free slots for the vehicle) 
     * @param vehicle vehicle class
     * @return number of free slots for this vehicle.
     */
    public long getAvailableCapacity(T vehicle) {
        return getAvailable(vehicle).count();
    }
    
    /**
     * Available capacity of the parking (number of free slots)
     * @return number of free slots
     */
    public long getAvailableCapacity() {
        return getAvailable().count();
    }

    /**
     * Setter of the pricing policy
     * @param pricingPolicy pricing policy
     */
    void setPricingPolicy(PricingPolicy<T> pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    /**
     * Add a new slot to the parking
     * @param id id of the slot
     * @param allocationStrategy the allocation strategy that will be used for that slot
     */
    void addSlot(Long id, Predicate<T> allocationStrategy) {
        slots.add(new Slot<>(id, allocationStrategy));
    }


    /**
     * Register a vehicle in the parking if there is any available slot that matches the allocation strategy
     * Synchronized method in order to make the registration thread safe
     * @param vehicle vehicle class
     * @param arrivalDateTime the arrival date and time.
     * @return {@link Registration} that contains the allocated slot if a free match is found
     */
    public synchronized Registration<T> register(T vehicle, LocalDateTime arrivalDateTime) {
        Registration<T> registration = new Registration<T>();
        getAvailable(vehicle).findFirst().ifPresent(
                slot -> registration.setSlot(slot.allocate(vehicle, arrivalDateTime)));
        return registration;
    }

    /**
     * Register the vehicle
     * Synchronized method in order to make the registration thread safe
     * @param vehicle vehicle class
     * @return {@link Registration} containing the allocated slot if a free match is found
     */
    public synchronized Registration<T> register(T vehicle) {
        return register(vehicle, LocalDateTime.now());
    }

    /**
     * Allow you to check out a vehicle.
     * Synchronized to make the checkOut thread safe
     * @param vehicle vehicle class
     * @param departureDateTime The departure date and time.
     * @return {@link Bill} containing slot snapshot and price.
     */
    public synchronized Bill<T> checkOut(T vehicle, LocalDateTime departureDateTime) {
        Slot<T> slot = slots.stream().filter(s -> s.getVehicle() == vehicle).findFirst().orElseThrow(
                () -> new ParkingException("Vehicle " + vehicle + " not found !")).free(departureDateTime);
        return new Bill<>(slot, pricingPolicy.computePrice(slot));
    }

    /**
     * Check out the vehicle
     * Synchronized to make the checkOut thread safe
     * @param vehicle The vehicle you want check out.
     * @return {@link Bill} containing slot snapshot and price.
     */
    public synchronized Bill<T> checkOut(T vehicle) {
        return checkOut(vehicle, LocalDateTime.now());
    }

    /**
     * Used by the builder to validate the parking set up
     */
    boolean validate() {
       
        if (this.slots.isEmpty()) {
            throw new ParkingException("The paking should contain at least one slot!");
        }
        if (this.pricingPolicy == null) {
            throw new PrincingPolicyException("Pricing pollicy is required to create a parking!");
        }
        return true;
    }

}
