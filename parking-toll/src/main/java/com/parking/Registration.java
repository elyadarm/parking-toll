package com.parking;

import java.time.LocalDateTime;

/**
 * This class is used for the return of {@link Parking#register(Object)}
 *
 * @param <T> The vehicle class
 */
public final class Registration<T> {

    private LocalDateTime creationDateTime;

    private Slot<T> slot;

    Registration() {
    	this.creationDateTime = LocalDateTime.now();
    }

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

    /**
     * Getter
     * @return Slot
     */
    public Slot<T> getSlot() {
        return slot;
    }
    
    /**
     *Setter
     * @param Slot
     */
    void setSlot(Slot<T> slot) {
        this.slot = slot;
    }

    /**
     * @return {@code true} if a slot was found during registration
     */
    public boolean isSuccessful() {
        return slot != null;
    }
    
}
