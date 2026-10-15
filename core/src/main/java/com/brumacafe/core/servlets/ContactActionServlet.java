package com.brumacafe.core.servlets;

import com.brumacafe.core.services.ContactService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths=/bin/brumacafe/contact/action",
        "sling.servlet.extensions=json"
})
public class ContactActionServlet extends SlingAllMethodsServlet {

    @Reference
    private ContactService contactService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Simple security check: block anonymous users
        if ("anonymous".equals(request.getResourceResolver().getUserID())) {
            response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Acesso negado.\"}");
            return;
        }

        String action = request.getParameter("action");
        String id = request.getParameter("id");

        if (StringUtils.isBlank(action) || StringUtils.isBlank(id)) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Parâmetros ausentes.\"}");
            return;
        }

        boolean success = false;
        try {
            switch (action) {
                case "MARK_READ":
                    success = contactService.updateMessageStatus(id, "READ");
                    break;
                case "MARK_UNREAD":
                    success = contactService.updateMessageStatus(id, "UNREAD");
                    break;
                case "TRASH":
                    success = contactService.updateMessageStatus(id, "TRASHED");
                    break;
                case "RECOVER":
                    success = contactService.updateMessageStatus(id, "READ");
                    break;
                case "HARD_DELETE":
                    success = contactService.deleteMessage(id);
                    break;
                default:
                    response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Ação inválida.\"}");
                    return;
            }
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erro ao processar ação.\"}");
            return;
        }

        if (success) {
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");
        } else {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Falha na operação.\"}");
        }
    }
}
