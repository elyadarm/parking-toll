package com.parking.exception;

public class PrincingPolicyException extends RuntimeException {

    /**
	 * Class used to manage pricing exceptions
	 */
	private static final long serialVersionUID = 1L;

	public PrincingPolicyException(String message) {
        super(message);
    }

}
