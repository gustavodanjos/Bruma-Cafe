package com.brumacafe.core.services;

public interface ContactService {
    
    /**
     * Saves a contact form submission to the repository.
     *
     * @param name    the name of the submitter
     * @param email   the email of the submitter
     * @param subject the subject of the message
     * @param message the content of the message
     * @return true if the message was successfully saved, false otherwise
     */
    boolean saveContactMessage(String name, String email, String subject, String message);
}
