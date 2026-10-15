package com.brumacafe.core.models.impl;

import com.brumacafe.core.models.ArticleInfoModel;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

@Model(
        adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = ArticleInfoModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArticleInfoModelImpl implements ArticleInfoModel {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleInfoModelImpl.class);
    private static final int WORDS_PER_MINUTE = 200;
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", LOCALE_PT_BR);

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private Resource resource;

    private String formattedDate;
    private int readingTime = 1;

    @PostConstruct
    protected void init() {
        calculateFormattedDate();
        calculateReadingTime();
    }

    private Page getPageToProcess() {
        Page pageToProcess = currentPage;
        
        if (pageToProcess == null && resource != null) {
            pageToProcess = resource.adaptTo(Page.class);
            if (pageToProcess == null) {
                if (resource.getResourceType().equals("cq:Page")) {
                    pageToProcess = resource.adaptTo(Page.class);
                } else if ("jcr:content".equals(resource.getName())) {
                    pageToProcess = resource.getParent().adaptTo(Page.class);
                } else {
                    Resource parent = resource.getParent();
                    while (parent != null) {
                        if ("cq:Page".equals(parent.getResourceType())) {
                            pageToProcess = parent.adaptTo(Page.class);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }
        return pageToProcess;
    }

    private void calculateFormattedDate() {
        Page pageToProcess = getPageToProcess();

        if (pageToProcess == null) {
            this.formattedDate = "";
            return;
        }

        ValueMap properties = pageToProcess.getProperties();
        Calendar date = properties.get("cq:lastReplicated", Calendar.class);
        
        if (date == null) {
            date = properties.get("jcr:created", Calendar.class);
        }

        if (date != null) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            this.formattedDate = zdt.format(DATE_FORMATTER);
        } else {
            this.formattedDate = "";
        }
    }

    private void calculateReadingTime() {
        Page pageToProcess = getPageToProcess();

        if (pageToProcess == null) {
            return;
        }

        Resource jcrContent = pageToProcess.getContentResource();
        if (jcrContent != null) {
            int totalWords = countWordsInContainer(jcrContent);
            this.readingTime = Math.max(1, (int) Math.ceil((double) totalWords / WORDS_PER_MINUTE));
        }
    }

    private int countWordsInContainer(Resource container) {
        int wordCount = 0;
        for (Resource child : container.getChildren()) {
            ValueMap props = child.getValueMap();
            String text = props.get("text", String.class);
            if (text != null && !text.isEmpty()) {
                String cleanText = text.replaceAll("<[^>]*>", " ");
                cleanText = cleanText.replaceAll("[^\\p{L}\\p{Nd}\\s]", " ").replaceAll("\\s+", " ").trim();
                if (!cleanText.isEmpty()) {
                    String[] words = cleanText.split(" ");
                    wordCount += words.length;
                }
            }
            if (child.hasChildren()) {
                wordCount += countWordsInContainer(child);
            }
        }
        return wordCount;
    }

    @Override
    public String getFormattedDate() {
        return formattedDate;
    }

    @Override
    public int getReadingTime() {
        return readingTime;
    }
}
