package com.parking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Slot of the Parking
 * @param <T> the vehicle class
 */
public final class Slot<T>{

	/**
	 * Slot id
	 */
    private final Long id;
    /**
     * The slot allocation strategy, to specify a condition for matching this kind of slots
     * You can put any kind of predicate here, it will be tested {@link Slot#test(T)} when allocating the slot for a vehicle T
     * This field is required for creating a slot
     */
    private final Predicate<T> allocationStrategy;
    /**
     * Vehicle class
     */
    private T vehicle;
    
    /**
     * Arrival date and time to the parking
     */
    private LocalDateTime arrivalDateTime;
    
    /**
     * Departure date and time from the parking
     */
    private LocalDateTime departureDateTime;

    
    /**
     * Constructor used in {@link Parking}
     * @param id
     * @param allocationStrategy the allocation strategy
     */
    Slot(final Long id, final Predicate<T> allocationStrategy) {
        this.id = id;
        Objects.requireNonNull(allocationStrategy, "allocationStrategy cannot be null !");
        this.allocationStrategy = allocationStrategy;
    }

    /**
     * Constructor of the Slot
     * @param id of the Slot
     * @param allocationStrategy the allocation strategy
     * @param vehicle the vehicle in the slot
     * @param arrivalDateTime the incoming date time of that vehicle
     * @param departureDateTime the outgoing date time of the vehicle
     */
    Slot(final Long id, final Predicate<T> allocationStrategy, T vehicle, LocalDateTime arrivalDateTime, LocalDateTime departureDateTime) {
        this.id = id;
        this.vehicle = vehicle;
        Objects.requireNonNull(allocationStrategy, "Allocation strategy cannot be null !");
        this.allocationStrategy = allocationStrategy;
        this.arrivalDateTime = arrivalDateTime;
        this.departureDateTime = departureDateTime;
    }

    /**
     * @param vehicle
     * @return {@code true} if the vehicle matches this kind of slot allocation strategy
     */
    boolean test(T vehicle) {
        return allocationStrategy.test(vehicle);
    }

    /**
     * @return {@code true} if the slot empty or not
     */
    boolean isFree() {
        return vehicle == null;
    }


    /**
     * Calculate stayDuration {@link Duration} based on {@link Slot#arrivalDateTime} and {@link Slot#departureDateTime}
     * @return {@link Duration} 
     */
    public Duration getStayDuration() {
        if (arrivalDateTime == null) {
        	return Duration.ZERO;
        }
        LocalDateTime departureDateTime = this.departureDateTime != null ? this.departureDateTime : LocalDateTime.now();
        return Duration.between(arrivalDateTime, departureDateTime);
    }

    /**
     * Allocate the slot for the vehicle
     * Used in {@link Parking#register(T)}
     * @param vehicle 
     * @param arrivalDateTime the arrival date time of the vehicle
     * @return a copy of the {@link Slot} for reporting needs
     */
    Slot<T> allocate(T vehicle, LocalDateTime arrivalDateTime) {
        Objects.requireNonNull(arrivalDateTime, "arrivalDateTime cannot be null !");
        Objects.requireNonNull(vehicle, "allocationStrategy cannot be null !");
        this.vehicle = vehicle;
        this.arrivalDateTime = arrivalDateTime;
        this.departureDateTime = null;
        return new Slot<T>(id, allocationStrategy, vehicle, arrivalDateTime, departureDateTime);
    }

    /**
     * 
     * Deallocate the slot, this is taking departureDateTime in order to offer some flexibility like freeing slots in the past
     * In the future we can add some business checks like forbidding using it in the future or the long past 
     * Used by {@link Parking#checkOut(T)}
     * @param departureDateTime the departure date time of the vehicle
     * @return a copy of {@link Slot} just before freeing it for reporting needs
     * @throws IllegalArgumentException if departureDateTime is before arrivalDateTime
     */
    Slot<T> free(LocalDateTime departureDateTime) {
        Objects.requireNonNull(departureDateTime);
        if (departureDateTime.isBefore(arrivalDateTime)) {
            throw new IllegalArgumentException("departureDateTime should be after arrivalDateTime!");
        }
        Slot<T> slot = new Slot<>(id, allocationStrategy, vehicle, arrivalDateTime, departureDateTime);
        this.arrivalDateTime = null;
        this.departureDateTime = null;
        this.vehicle = null;
        return slot;
    }
    /**
     * Deallocate the slot
     * @return a copy of {@link Slot}
     */
    Slot<T> free() {
    	LocalDateTime departureDateTime = LocalDateTime.now();
    	return free(departureDateTime);
    }

    /**
     * Getter
     * @return the id of the slot
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter
     * @return the allocation strategy
     */
    public Predicate<T> getAllocationStrategy() {
        return allocationStrategy;
    }


    /**
     * Getter
     * @return arrival date and time
     */
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    /**
     * Getter
     * @return departure date and time
     */
    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }
    /**
     * Getter
     * @return the vehicle
     */
    public T getVehicle() {
        return vehicle;
    }


}