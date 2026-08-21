package com.brumacafe.core.servlets;

import com.brumacafe.core.models.ContactMessage;
import com.brumacafe.core.services.ContactService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import javax.servlet.Servlet;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=/bin/brumacafe/contact/list",
        "sling.servlet.extensions=json"
})
public class ContactListServlet extends SlingSafeMethodsServlet {

    @Reference
    private ContactService contactService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Simple security check: block anonymous users
        if ("anonymous".equals(request.getResourceResolver().getUserID())) {
            response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Acesso negado.\"}");
            return;
        }

        List<ContactMessage> messages = contactService.getContactMessages();
        
        // Manual JSON generation to avoid external dependencies
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (int i = 0; i < messages.size(); i++) {
            ContactMessage msg = messages.get(i);
            jsonBuilder.append("{");
            jsonBuilder.append("\"id\": \"").append(escapeJson(msg.getId())).append("\",");
            jsonBuilder.append("\"name\": \"").append(escapeJson(msg.getName())).append("\",");
            jsonBuilder.append("\"email\": \"").append(escapeJson(msg.getEmail())).append("\",");
            jsonBuilder.append("\"subject\": \"").append(escapeJson(msg.getSubject())).append("\",");
            jsonBuilder.append("\"message\": \"").append(escapeJson(msg.getMessage())).append("\",");
            jsonBuilder.append("\"status\": \"").append(escapeJson(msg.getStatus())).append("\",");
            jsonBuilder.append("\"date\": \"").append(msg.getDate() != null ? sdf.format(msg.getDate().getTime()) : "").append("\"");
            jsonBuilder.append("}");
            
            if (i < messages.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        
        jsonBuilder.append("]");

        response.setStatus(SlingHttpServletResponse.SC_OK);
        response.getWriter().write(jsonBuilder.toString());
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
