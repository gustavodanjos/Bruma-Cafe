package com.brumacafe.core.models;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(
    adaptables = Resource.class,
    resourceType = ContentGridModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ContentGridModel {

    public static final String RESOURCE_TYPE = "brumacafe/components/content-grid";

    @ChildResource(name = "items")
    private List<ContentGridItemModel> items;

    public List<ContentGridItemModel> getItems() {
        return items != null ? items : Collections.emptyList();
    }

    public boolean isEmpty() {
        return getItems().isEmpty();
    }
}