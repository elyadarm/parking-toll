package com.parking;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;


/**
 * Pricing policy used for calculating the Bill when the vehicle leaves the parking
 * @param <T> the vehicle class
 */
public interface PricingPolicy<T> {

    /**
     * The method used for the price calculation
     * @param slot {@link Slot}
     * @return the price {@link BigDecimal}
     */
	BigDecimal computePrice(Slot<T> slot);
	
	
    /**
     * HOURLY pricing policy: the price as function of number of hours of stay at the parking
     * @param perHourRate the rate/hour
     * @param <T> the vehicle class
     * @return {@link PricingPolicy} Hourly pricing policy
     */
    static <T> PricingPolicy<T> HOURLY(BigDecimal perHourRate) {
        return slot -> hourlyCalculation(slot.getStayDuration(), perHourRate);
    }
    
    /**
     * Method of hourly calculation: perHourRate * stayDuration
     * @param stayDuration duration of stay in the parking
     * @param perHourRate the price per hour
     * @return the price {@link BigDecimal}
     */
    static BigDecimal hourlyCalculation(Duration stayDuration, BigDecimal perHourRate) {
    	BigDecimal duration = new BigDecimal(stayDuration.toMinutes()/60d);
    	BigDecimal price = duration.multiply(perHourRate);
        return price;
    }

    
    /**
     * FIXED pricing policy: the price is a fixed amount
     * @param fixedAmount fixed price 
     * @param <T> the vehicle class
     * @return {@link PricingPolicy} Fixed pricing policy
     */
    static <T> PricingPolicy<T> FIXED(BigDecimal fixedAmount) {
        return slot -> fixedAmount;
    }

    /**
     * SUM of 2 pricing policies
     * @param p1 first policy
     * @param p2 second policy
     * @param <T> vehicle class
     * @return The new pricing policy(p1 + p2) {@link PricingPolicy}
     */
    static <T> PricingPolicy<T> SUM(PricingPolicy<T> p1, PricingPolicy<T> p2) {
        return slot -> p1.computePrice(slot).add(p2.computePrice(slot));
    }


    /**
     * HOURLY AND FIXED policy: fixedAmount + (Hourly price)
     * @param perHourRate the price/hour
     * @param fixedAmount fixed price amount
     * @param <T> the Class you use for your cars
     * @return {@link PricingPolicy} resulting price policy
     */
    static <T> PricingPolicy<T> HOURLY_AND_FIXED(BigDecimal perHourRate, BigDecimal fixedAmount) {
        return SUM(HOURLY(perHourRate), FIXED(fixedAmount));
    }

}
