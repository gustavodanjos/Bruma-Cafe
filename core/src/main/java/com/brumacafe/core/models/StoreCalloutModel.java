package com.brumacafe.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    resourceType = StoreCalloutModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class StoreCalloutModel {

    public static final String RESOURCE_TYPE = "brumacafe/components/store-callout";

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String buttonLabel;

    @ValueMapValue
    private String buttonLink;
    
    @ValueMapValue
    private String fileReference;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getButtonLink() {
        return isValidLink(buttonLink) ? buttonLink.trim() : null;
    }
    
    public String getFileReference() {
        return fileReference;
    }

    public boolean isEmpty() {
        return !hasText(title) || !hasText(buttonLink);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidLink(String link) {
        if (!hasText(link)) {
            return false;
        }
        String lower = link.trim().toLowerCase();
        return !lower.startsWith("javascript:")
            && !lower.startsWith("data:")
            && !lower.startsWith("vbscript:");
    }
}
