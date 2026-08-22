package com.brumacafe.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class StoreCalloutModelTest {

    public final AemContext context = new AemContext();

    private StoreCalloutModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(StoreCalloutModel.class);
    }

    @Test
    void testHappyPath() {
        Map<String, Object> props = new HashMap<>();
        props.put("title", "Visit Our Store");
        props.put("text", "Get the best coffee beans.");
        props.put("buttonLabel", "Shop Now");
        props.put("buttonLink", "/content/brumacafe/us/en/store");
        props.put("fileReference", "/content/dam/brumacafe/store.jpg");
        props.put("sling:resourceType", StoreCalloutModel.RESOURCE_TYPE);

        Resource component = context.create().resource("/content/store-callout", props);

        model = component.adaptTo(StoreCalloutModel.class);

        assertNotNull(model);
        assertFalse(model.isEmpty());
        assertEquals("Visit Our Store", model.getTitle());
        assertEquals("Get the best coffee beans.", model.getText());
        assertEquals("Shop Now", model.getButtonLabel());
        assertEquals("/content/brumacafe/us/en/store", model.getButtonLink());
        assertEquals("/content/dam/brumacafe/store.jpg", model.getFileReference());
    }

    @Test
    void testEmptyModel() {
        Resource component = context.create().resource("/content/store-callout", 
            "sling:resourceType", StoreCalloutModel.RESOURCE_TYPE);

        model = component.adaptTo(StoreCalloutModel.class);

        assertNotNull(model);
        assertTrue(model.isEmpty());
        assertNull(model.getTitle());
        assertNull(model.getButtonLink());
    }

    @Test
    void testInvalidLink() {
        Map<String, Object> props = new HashMap<>();
        props.put("title", "Visit");
        props.put("buttonLink", "javascript:alert(1)");
        props.put("sling:resourceType", StoreCalloutModel.RESOURCE_TYPE);

        Resource component = context.create().resource("/content/store-callout", props);

        model = component.adaptTo(StoreCalloutModel.class);

        assertNotNull(model);
        assertNull(model.getButtonLink(), "Should filter out javascript links");
        assertFalse(model.isEmpty(), "Model should not be empty because the raw property has text");
    }
}
