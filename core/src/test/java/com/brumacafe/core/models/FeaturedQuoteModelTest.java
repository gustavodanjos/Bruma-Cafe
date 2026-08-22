package com.brumacafe.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class FeaturedQuoteModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(FeaturedQuoteModel.class);
    }

    @Test
    void testAuthorIsReturnedWhenConfigured() {
        // 1. ARRANGE
        context.create().resource(
            "/content/featured-quote",
            "sling:resourceType", FeaturedQuoteModel.RESOURCE_TYPE,
            "author", "Bruma Café"
        );

        context.currentResource("/content/featured-quote");

        // 2. ACT
        FeaturedQuoteModel model =
            context.currentResource().adaptTo(FeaturedQuoteModel.class);

        // 3. ASSERT
        assertNotNull(
            model,
            "The FeaturedQuoteModel should not be null"
        );

        assertEquals(
            "Bruma Café",
            model.getAuthor(),
            "The author should match the configured value"
        );
    }

    @Test
    void testAuthorIsOptional() {
        // 1. ARRANGE
        context.create().resource(
            "/content/featured-quote",
            "sling:resourceType", FeaturedQuoteModel.RESOURCE_TYPE
        );

        context.currentResource("/content/featured-quote");

        // 2. ACT
        FeaturedQuoteModel model =
            context.currentResource().adaptTo(FeaturedQuoteModel.class);

        // 3. ASSERT
        assertNotNull(
            model,
            "The FeaturedQuoteModel should not be null"
        );

        assertNull(
            model.getAuthor(),
            "The author should be null when it is not configured"
        );
    }
}