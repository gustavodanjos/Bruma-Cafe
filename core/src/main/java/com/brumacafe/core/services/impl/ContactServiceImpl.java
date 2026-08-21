package com.brumacafe.core.services.impl;

import com.brumacafe.core.models.ContactMessage;
import com.brumacafe.core.services.ContactService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;
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
            properties.put("status", "UNREAD");

            resourceResolver.create(parentResource, nodeName, properties);
            resourceResolver.commit();

            // Obter o TaskManager através do ResourceResolver (forma correta no AEM)
            TaskManager taskManager = resourceResolver.adaptTo(TaskManager.class);

            if (taskManager != null) {
                try {
                    Task newTask = taskManager.getTaskManagerFactory().newTask("Notification");
                    newTask.setName("Bruma Café - Nova Mensagem: " + subject);
                    newTask.setDescription("Remetente: " + name + " (" + email + ")\n\nMensagem:\n" + message);
                    newTask.setCurrentAssignee("administrators"); 
                    newTask.setContentPath(MESSAGES_PATH + "/" + nodeName);
                    taskManager.createTask(newTask);
                    LOG.info("Notificação enviada para a AEM Inbox com sucesso.");
                } catch (TaskManagerException e) {
                    LOG.error("Erro ao criar notificação na Inbox para a mensagem {}", nodeName, e);
                }
            } else {
                LOG.warn("TaskManager não pôde ser adaptado do ResourceResolver. Notificação da Inbox ignorada.");
            }

            LOG.info("Mensagem de contato persistida no JCR com sucesso. Email: {}", email);
            return true;

        } catch (Exception e) {
            LOG.error("Erro ao salvar a mensagem de contato no JCR", e);
            return false;
        }
    }

    @Override
    public List<ContactMessage> getContactMessages() {
        List<ContactMessage> messages = new ArrayList<>();
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, CONTACT_SERVICE);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource parentResource = resourceResolver.getResource(MESSAGES_PATH);
            
            if (parentResource != null) {
                for (Resource child : parentResource.getChildren()) {
                    ValueMap properties = child.getValueMap();
                    String name = properties.get("name", String.class);
                    String email = properties.get("email", String.class);
                    String subject = properties.get("subject", String.class);
                    String messageText = properties.get("message", String.class);
                    Calendar date = properties.get("date", Calendar.class);

                    String id = child.getName();
                    String status = properties.get("status", "UNREAD");
                    Calendar trashedDate = properties.get("trashedDate", Calendar.class);

                    if (name != null && email != null) {
                        messages.add(new ContactMessage(id, name, email, subject, messageText, status, date, trashedDate));
                    }
                }
                
                // Sort by date descending
                messages.sort((m1, m2) -> {
                    if (m1.getDate() == null || m2.getDate() == null) return 0;
                    return m2.getDate().compareTo(m1.getDate());
                });
            }
        } catch (Exception e) {
            LOG.error("Erro ao recuperar mensagens de contato do JCR", e);
        }
        return messages;
    }

    @Override
    public boolean updateMessageStatus(String id, String status) {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, CONTACT_SERVICE);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource messageResource = resourceResolver.getResource(MESSAGES_PATH + "/" + id);
            if (messageResource != null) {
                org.apache.sling.api.resource.ModifiableValueMap properties = messageResource.adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class);
                if (properties != null) {
                    properties.put("status", status);
                    if ("TRASHED".equals(status)) {
                        properties.put("trashedDate", Calendar.getInstance());
                    } else {
                        properties.remove("trashedDate");
                    }
                    resourceResolver.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error("Erro ao atualizar status da mensagem de contato", e);
        }
        return false;
    }

    @Override
    public boolean deleteMessage(String id) {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, CONTACT_SERVICE);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource messageResource = resourceResolver.getResource(MESSAGES_PATH + "/" + id);
            if (messageResource != null) {
                resourceResolver.delete(messageResource);
                resourceResolver.commit();
                return true;
            }
        } catch (Exception e) {
            LOG.error("Erro ao deletar mensagem de contato", e);
        }
        return false;
    }
}
