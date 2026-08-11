package com.brumacafe.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    resourceType = MainBannerModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MainBannerModel {

    public static final String RESOURCE_TYPE = "brumacafe/components/main-banner";

    @ValueMapValue(name = "jcr:title")
    private String title;

    @ValueMapValue(name = "jcr:description")
    private String description;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String alt;

    @ChildResource(name = "actions")
    private Resource actions;

    private String buttonText;
    private String buttonLink;
    private String buttonTarget;

    @PostConstruct
    protected void init() {
        if (actions == null) {
            return;
        }

        Resource firstAction = actions.getChild("item0");

        if (firstAction == null) {
            return;
        }

        ValueMap actionProperties = firstAction.getValueMap();

        buttonText = actionProperties.get("text", String.class);
        buttonLink = actionProperties.get("link", String.class);
        buttonTarget = actionProperties.get("linkTarget", String.class);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFileReference() {
        return fileReference;
    }

    public String getAlt() {
        return alt;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getButtonLink() {
        return buttonLink;
    }

    public String getButtonTarget() {
        return buttonTarget;
    }

    public boolean isButtonVisible() {
        return hasText(buttonText) && hasText(buttonLink);
    }

    public boolean isEmpty() {
        return !hasText(title)
            && !hasText(description)
            && !hasText(fileReference);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}