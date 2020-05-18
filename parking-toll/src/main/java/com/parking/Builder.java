package com.parking;

import java.util.function.Predicate;

/**
 * The parking builder used for creating {@link Parking} instance
 * Specify pricing policy and parking slots
 * @param <T> the vehicle class
 * @author Maad
 */
public class Builder<T> {

	/**
	 * A simple slots counter
	 */
    private Long slotsCounter = 0L;

    /**
     * Parking instance
     */
    private final Parking<T> parking;

    /**
     * Builder Constructor to initialize the parking instance  
     */
    Builder() {
        this.parking = new Parking<>();
    }

    /**
     * @return next id number, for now it's a simple increment!
     * Will be improved in the future to use different generation strategies
     */
    private Long generateId() {
    	slotsCounter= Long.sum(slotsCounter, 1L);
    	return slotsCounter;
    }

    /**
     * Set the pricing policy {@link PricingPolicy} that will be used for the {@link Bill}
     * @param pricingPolicy the pricing policy used
     * @return {@link Builder} the parking builder
     */
    public Builder<T> setPricingPolicy(PricingPolicy<T> pricingPolicy) {
        this.parking.setPricingPolicy(pricingPolicy);
        return this;
    }
    
    /**
     * Add a new slot in the parking
     * @param allocationStrategy the {@link Predicate} used by the slot
     * @return {@link Builder} the parking builder
     */
    public Builder<T> addSlot(Predicate<T> allocationStrategy) {
        return addSlot(generateId(), allocationStrategy);
    }
    
    /**
     * Add a new slot in the parking
     * @param id of the {@link Slot}
     * @param alocationStrategy allocation strategy for the slot
     * @return {@link Builder} the parking builder
     */
    public Builder<T> addSlot(Long id, Predicate<T> alocationStrategy) {
        this.parking.addSlot(id, alocationStrategy);
        return this;
    }

    /**
     * Create multiple slots in the parking
     * @param allocationStrategy the {@link Predicate} of created slots
     * @param numberOfSlots the number of new slot to create
     * @return {@link Builder} the parking builder
     */
    public Builder<T> addSlots(Predicate<T> allocationStrategy, int numberOfSlots) {
        for (int i = 0; i < numberOfSlots; i++) {
        	addSlot(allocationStrategy);
        }
        return this;
    }

    
    /**
     * The last step of the building, validate {@link Parking#validate()}
     * @return the corresponding {@link Parking}
     */
    public Parking<T> build() {
        this.parking.validate();
        return this.parking;
    }
}
