package com.brumacafe.core.models;

import com.adobe.cq.wcm.core.components.models.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

@Model(
    adaptables = SlingHttpServletRequest.class,
    resourceType = FeaturedQuoteModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FeaturedQuoteModel {

    public static final String RESOURCE_TYPE =
        "brumacafe/components/featured-quote";

    @Self
    @Via(type = ResourceSuperType.class)
    private Text delegate;

    @ValueMapValue
    private String author;

    public String getText() {
        return delegate != null ? delegate.getText() : null;
    }

    public boolean isRichText() {
        return delegate != null && delegate.isRichText();
    }

    public String getAuthor() {
        return author;
    }

    public boolean isEmpty() {
        String text = getText();
        return text == null || text.trim().isEmpty();
    }
}