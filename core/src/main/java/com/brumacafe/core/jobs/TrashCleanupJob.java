package com.brumacafe.core.jobs;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

@Component(service = Runnable.class, property = {
        "scheduler.expression=0 0 * * * ?",
        "scheduler.concurrent=false",
        "scheduler.name=Bruma Cafe - Limpeza da Lixeira de Contatos"
})
public class TrashCleanupJob implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TrashCleanupJob.class);
    private static final String CONTACT_SERVICE = "contact-service";
    private static final String MESSAGES_PATH = "/var/brumacafe/contact";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void run() {
        LOG.info("Iniciando rotina de limpeza da lixeira de contatos...");
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, CONTACT_SERVICE);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            Resource parentResource = resourceResolver.getResource(MESSAGES_PATH);
            
            if (parentResource != null) {
                Calendar twentyFourHoursAgo = Calendar.getInstance();
                twentyFourHoursAgo.add(Calendar.HOUR_OF_DAY, -24);
                int deletedCount = 0;

                for (Resource child : parentResource.getChildren()) {
                    String status = child.getValueMap().get("status", String.class);
                    Calendar trashedDate = child.getValueMap().get("trashedDate", Calendar.class);

                    if ("TRASHED".equals(status) && trashedDate != null) {
                        if (trashedDate.before(twentyFourHoursAgo)) {
                            resourceResolver.delete(child);
                            deletedCount++;
                        }
                    }
                }
                
                if (deletedCount > 0) {
                    resourceResolver.commit();
                    LOG.info("Limpeza concluída. {} mensagens excluídas definitivamente da lixeira.", deletedCount);
                } else {
                    LOG.info("Limpeza concluída. Nenhuma mensagem velha encontrada na lixeira.");
                }
            }
        } catch (Exception e) {
            LOG.error("Erro ao executar a rotina de limpeza da lixeira", e);
        }
    }
}
