package com.brumacafe.core.models.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class ArticleInfoModelImplTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(ArticleInfoModelImpl.class);
    }

    @Test
    void testFormattedDateWithLastReplicated() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JULY, 8, 10, 0);

        Map<String, Object> pageProperties = new HashMap<>();
        pageProperties.put("cq:lastReplicated", calendar);

        context.create().page("/content/brumacafe/artigo", "brumacafe/components/page", pageProperties);
        context.currentPage("/content/brumacafe/artigo");

        ArticleInfoModelImpl model = context.request().adaptTo(ArticleInfoModelImpl.class);

        assertNotNull(model);
        assertEquals("8 de julho de 2024", model.getFormattedDate());
    }

    @Test
    void testFormattedDateFallbackToJcrCreated() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JANUARY, 15, 10, 0);

        Map<String, Object> pageProperties = new HashMap<>();
        pageProperties.put("jcr:created", calendar);

        context.create().page("/content/brumacafe/artigo2", "brumacafe/components/page", pageProperties);
        context.currentPage("/content/brumacafe/artigo2");

        ArticleInfoModelImpl model = context.request().adaptTo(ArticleInfoModelImpl.class);

        assertNotNull(model);
        assertEquals("15 de janeiro de 2023", model.getFormattedDate());
    }

    @Test
    void testReadingTimeWithHtmlText() {
        context.create().page("/content/brumacafe/artigo3", "brumacafe/components/page");
        context.currentPage("/content/brumacafe/artigo3");

        Resource container = context.create().resource("/content/brumacafe/artigo3/jcr:content/root/container");

        Map<String, Object> textProps = new HashMap<>();
        textProps.put("text", "<p>Um dois <strong>três</strong> quatro.</p>");
        context.create().resource(container, "text1", textProps);

        StringBuilder longText = new StringBuilder("<p>");
        for (int i = 0; i < 300; i++) {
            longText.append("palavra ");
        }
        longText.append("</p>");
        
        Map<String, Object> textProps2 = new HashMap<>();
        textProps2.put("text", longText.toString());
        context.create().resource(container, "text2", textProps2);

        ArticleInfoModelImpl model = context.request().adaptTo(ArticleInfoModelImpl.class);

        assertNotNull(model);
        assertEquals(2, model.getReadingTime());
    }
    
    @Test
    void testReadingTimeMinimumOneMinute() {
        context.create().page("/content/brumacafe/artigo4", "brumacafe/components/page");
        context.currentPage("/content/brumacafe/artigo4");

        Resource container = context.create().resource("/content/brumacafe/artigo4/jcr:content/root/container");

        Map<String, Object> textProps = new HashMap<>();
        textProps.put("text", "<p>Texto super curto.</p>");
        context.create().resource(container, "text1", textProps);

        ArticleInfoModelImpl model = context.request().adaptTo(ArticleInfoModelImpl.class);

        assertNotNull(model);
        assertEquals(1, model.getReadingTime());
    }
}
