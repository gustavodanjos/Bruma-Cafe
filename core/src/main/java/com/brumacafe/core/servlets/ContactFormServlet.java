package com.brumacafe.core.servlets;

import com.brumacafe.core.services.ContactService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.regex.Pattern;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.resourceTypes=brumacafe/components/contactform",
        "sling.servlet.selectors=contact",
        "sling.servlet.extensions=json"
})
public class ContactFormServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContactFormServlet.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Reference
    private ContactService contactService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        if (StringUtils.isAnyBlank(name, email, message)) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Preencha todos os campos obrigatórios.\"}");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Endereço de e-mail inválido.\"}");
            return;
        }

       
        boolean isSaved = contactService.saveContactMessage(name, email, subject, message);

        if (isSaved) {
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true, \"message\": \"Mensagem enviada com sucesso!\"}");
        } else {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erro interno ao processar a mensagem no servidor.\"}");
        }
    }
}
