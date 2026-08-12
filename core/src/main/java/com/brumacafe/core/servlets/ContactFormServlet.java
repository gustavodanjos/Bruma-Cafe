package com.brumacafe.core.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.sling.api.servlets.HttpConstants;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.resourceTypes=brumacafe/components/contactform",
        "sling.servlet.selectors=contact",
        "sling.servlet.extensions=json"
})
public class ContactFormServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContactFormServlet.class);
    private static final String CONTACT_SERVICE = "contact-service";
    private static final String MESSAGES_PATH = "/var/brumacafe/contact";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

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

        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, CONTACT_SERVICE);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource parentResource = resourceResolver.getResource(MESSAGES_PATH);
            if (parentResource == null) {
                LOG.error("O caminho base para mensagens não existe: {}", MESSAGES_PATH);
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Erro interno no servidor (MESSAGES_PATH_NOT_FOUND).\"}");
                return;
            }

            String nodeName = Calendar.getInstance().getTimeInMillis() + "-" + UUID.randomUUID().toString();
            
            Map<String, Object> properties = new HashMap<>();
            properties.put("jcr:primaryType", "nt:unstructured");
            properties.put("name", name);
            properties.put("email", email);
            properties.put("subject", StringUtils.defaultString(subject, ""));
            properties.put("message", message);
            properties.put("date", Calendar.getInstance());

            resourceResolver.create(parentResource, nodeName, properties);
            resourceResolver.commit();

            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true, \"message\": \"Mensagem enviada com sucesso!\"}");

            LOG.info("Mensagem de contato recebida de {}", email);

        } catch (Exception e) {
            LOG.error("Erro ao salvar a mensagem de contato no JCR", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erro ao salvar a mensagem no banco de dados.\"}");
        }
    }
}
