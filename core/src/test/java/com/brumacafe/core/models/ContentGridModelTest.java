package com.brumacafe.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class ContentGridModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(
            ContentGridModel.class,
            ContentGridItemModel.class
        );
    }

    @Test
    void shouldReturnConfiguredItems() {
        context.create().resource(
            "/content/content-grid",
            "sling:resourceType", ContentGridModel.RESOURCE_TYPE
        );

        context.create().resource(
            "/content/content-grid/items/item0",
            "fileReference", "/content/dam/brumacafe/image-1.jpg",
            "alt", "Coffee plantation",
            "title", "Origin",
            "text", "Coffee grown by local producers."
        );

        context.create().resource(
            "/content/content-grid/items/item1",
            "fileReference", "/content/dam/brumacafe/image-2.jpg",
            "alt", "Coffee beans",
            "title", "Process",
            "text", "From harvest to roasting."
        );

        context.currentResource("/content/content-grid");

        ContentGridModel model =
            context.currentResource().adaptTo(ContentGridModel.class);

        assertNotNull(model);
        assertFalse(model.isEmpty());

        List<ContentGridItemModel> items = model.getItems();

        assertEquals(2, items.size());

        assertEquals(
            "/content/dam/brumacafe/image-1.jpg",
            items.get(0).getFileReference()
        );
        assertEquals("Coffee plantation", items.get(0).getAlt());
        assertEquals("Origin", items.get(0).getTitle());
        assertEquals(
            "Coffee grown by local producers.",
            items.get(0).getText()
        );

        assertEquals("Process", items.get(1).getTitle());
    }

    @Test
    void shouldReturnEmptyListWhenItemsAreMissing() {
        context.create().resource(
            "/content/content-grid",
            "sling:resourceType", ContentGridModel.RESOURCE_TYPE
        );

        context.currentResource("/content/content-grid");

        ContentGridModel model =
            context.currentResource().adaptTo(ContentGridModel.class);

        assertNotNull(model);
        assertTrue(model.getItems().isEmpty());
        assertTrue(model.isEmpty());
    }
}