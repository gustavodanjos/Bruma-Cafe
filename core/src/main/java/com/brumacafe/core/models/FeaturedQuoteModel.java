package com.brumacafe.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    resourceType = FeaturedQuoteModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FeaturedQuoteModel {

    public static final String RESOURCE_TYPE =
        "brumacafe/components/featured-quote";

    @ValueMapValue
    private String author;

    public String getAuthor() {
        return author;
    }
}