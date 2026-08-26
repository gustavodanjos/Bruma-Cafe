package com.brumacafe.core.services;

import com.brumacafe.core.models.ContactMessage;
import java.util.List;

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

    /**
     * Retrieves a list of contact messages from the repository.
     *
     * @return a list of ContactMessage objects
     */
    List<ContactMessage> getContactMessages();

    /**
     * Updates the status of a specific contact message.
     *
     * @param id     the node name of the message
     * @param status the new status (e.g., READ, UNREAD, TRASHED)
     * @return true if successful, false otherwise
     */
    boolean updateMessageStatus(String id, String status);

    /**
     * Permanently deletes a contact message from the repository.
     *
     * @param id the node name of the message
     * @return true if successful, false otherwise
     */
    boolean deleteMessage(String id);
}
