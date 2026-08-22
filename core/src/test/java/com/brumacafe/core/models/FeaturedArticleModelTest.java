package com.brumacafe.core.models;

import com.day.cq.wcm.api.Page;
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
class FeaturedArticleModelTest {

    public final AemContext context = new AemContext();

    private FeaturedArticleModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(FeaturedArticleModel.class);
        
        // Create an article page to be featured
        Page articlePage = context.create().page("/content/brumacafe/us/en/article1", 
            "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo", 
            "jcr:title", "Coffee Origins",
            "jcr:description", "Learn about coffee origins");
        
        // Create an image node to test cover image extraction
        context.create().resource(articlePage.getContentResource().getPath() + "/image",
            "fileReference", "/content/dam/brumacafe/coffee.jpg");
    }

    @Test
    void testHappyPath_ValidArticle() {
        Map<String, Object> props = new HashMap<>();
        props.put("articlePath", "/content/brumacafe/us/en/article1");
        props.put("badgeLabel", "New");
        props.put("ctaText", "Read More");

        Resource component = context.create().resource("/content/featured", props);
        context.currentResource(component);

        model = context.request().adaptTo(FeaturedArticleModel.class);

        assertNotNull(model, "Model should be successfully adapted");
        assertFalse(model.isEmpty(), "Model should not be empty when valid path is provided");
        assertEquals("New", model.getBadgeLabel());
        assertEquals("Read More", model.getCtaText());
        assertNotNull(model.getArticle(), "ArticleDTO should not be null");
        assertEquals("Coffee Origins", model.getArticle().getTitle());
        assertEquals("Learn about coffee origins", model.getArticle().getDescription());
        assertEquals("/content/dam/brumacafe/coffee.jpg", model.getArticle().getImagePath());
    }

    @Test
    void testEmptyArticlePath() {
        // Component without articlePath
        Resource component = context.create().resource("/content/featured");
        context.currentResource(component);
        
        model = context.request().adaptTo(FeaturedArticleModel.class);
        
        assertNotNull(model);
        assertTrue(model.isEmpty(), "Model should be empty when articlePath is blank");
        assertEquals("Destaque", model.getBadgeLabel(), "Should return default badge label");
        assertEquals("Ler artigo completo", model.getCtaText(), "Should return default CTA text");
    }
    
    @Test
    void testInvalidArticlePath() {
        Map<String, Object> props = new HashMap<>();
        props.put("articlePath", "/content/brumacafe/us/en/invalid");
        Resource component = context.create().resource("/content/featured", props);
        context.currentResource(component);

        model = context.request().adaptTo(FeaturedArticleModel.class);

        assertNotNull(model);
        assertTrue(model.isEmpty(), "Model should be empty when articlePath points to non-existent page");
    }
}
