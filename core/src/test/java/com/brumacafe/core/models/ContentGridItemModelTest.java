package com.brumacafe.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class ContentGridItemModelTest {

    public final AemContext context = new AemContext();

    private ContentGridItemModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(ContentGridItemModel.class);
    }

    @Test
    void testHappyPath() {
        Map<String, Object> props = new HashMap<>();
        props.put("fileReference", "/content/dam/brumacafe/image.jpg");
        props.put("alt", "Image Alt");
        props.put("title", "Grid Item Title");
        props.put("text", "Grid Item Text");

        Resource component = context.create().resource("/content/grid-item", props);

        model = component.adaptTo(ContentGridItemModel.class);

        assertNotNull(model);
        assertEquals("/content/dam/brumacafe/image.jpg", model.getFileReference());
        assertEquals("Image Alt", model.getAlt());
        assertEquals("Grid Item Title", model.getTitle());
        assertEquals("Grid Item Text", model.getText());
    }
}
