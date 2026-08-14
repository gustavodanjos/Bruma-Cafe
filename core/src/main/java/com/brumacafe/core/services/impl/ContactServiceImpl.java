package com.brumacafe.core.services.impl;

import com.brumacafe.core.services.ContactService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component(service = ContactService.class)
public class ContactServiceImpl implements ContactService {

    private static final Logger LOG = LoggerFactory.getLogger(ContactServiceImpl.class);
    private static final String CONTACT_SERVICE = "contact-service";
    private static final String MESSAGES_PATH = "/var/brumacafe/contact";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public boolean saveContactMessage(String name, String email, String subject, String message) {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, CONTACT_SERVICE);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource parentResource = resourceResolver.getResource(MESSAGES_PATH);
            
            if (parentResource == null) {
                LOG.error("O caminho base para mensagens não existe no JCR: {}", MESSAGES_PATH);
                return false;
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

            LOG.info("Mensagem de contato persistida no JCR com sucesso. Email: {}", email);
            return true;

        } catch (Exception e) {
            LOG.error("Erro ao salvar a mensagem de contato no JCR", e);
            return false;
        }
    }
}
