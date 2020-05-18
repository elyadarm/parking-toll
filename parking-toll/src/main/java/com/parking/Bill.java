package com.parking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This class exposes bill details return when the Vehicle leaves the Parking
 * @param <T> The Vehicle class
 * @author Maad
 */
public final class Bill<T> {

	/**
	 * This slot instance is a snapshot of the instance before the checkout
	 * in order to keep all the details for billing purposes 
	 */
    private final Slot<T> slotSnapShot;
    private final BigDecimal price;
    private LocalDateTime creationDateTime;


    /**
     * Constructor of the Bill
     * @param slot related to the bill 
     * @param price 
     */
    Bill(Slot<T> slot, BigDecimal price) {
    	this.creationDateTime = LocalDateTime.now();
        this.slotSnapShot = slot;
        this.price = price;
    }

    public Slot<T> getSlot() {
        return slotSnapShot;
    }

    public BigDecimal getPrice() {
        return price;
    }

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}
}
