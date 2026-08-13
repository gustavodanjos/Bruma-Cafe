package com.brumacafe.core.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class MainBannerModelTest {

    public final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(MainBannerModel.class);
    }

    @Test
    void shouldShowButtonWhenTextAndLinkAreProvided() {
        MainBannerModel model = createModel(
            "buttonText", "Explore our coffees",
            "buttonLink", "/content/brumacafe/us/en"
        );

        assertNotNull(model, "The model should not be null when created");
        assertTrue(model.isButtonVisible(), "The button should be visible when both text and link are provided");
    }

    @Test
    void shouldHideButtonWhenLinkIsMissing() {
        MainBannerModel model = createModel(
            "buttonText", "Explore our coffees"
        );
        assertNotNull(model, "The model should not be null when created");
        assertFalse(model.isButtonVisible(), "The button should be hidden when the link is missing");
    }

    @Test
    void shouldHideButtonWhenTextIsMissing() {
        MainBannerModel model = createModel(
            "buttonLink", "/content/brumacafe/us/en"
        );

        assertNotNull(model, "The model should not be null when created");
        assertFalse(model.isButtonVisible(), "The button should be hidden when the text is missing");
    }

    @Test
    void shouldHideButtonWhenLinkIsBlank() {
        MainBannerModel model = createModel(
            "buttonText", "Explore our coffees",
            "buttonLink", "   "
        );

        assertNotNull(model, "The model should not be null when created");
        assertFalse(model.isButtonVisible(), "The button should be hidden when the link contains only spaces");
    }

    @Test
    void shouldHideButtonWhenLinkHasMaliciousScheme() {
        
        MainBannerModel modelJavascript = createModel(
            "buttonText", "Click here",
            "buttonLink", "javascript:alert('xss')"
        );

        MainBannerModel modelData = createModel(
            "buttonText", "Click here",
            "buttonLink", "data:text/html,<script>alert(1)</script>"
        );

        MainBannerModel modelVbscript = createModel(
            "buttonText", "Click here",
            "buttonLink", "vbscript:msgbox(\"XSS\")"
        );

        assertFalse(modelJavascript.isButtonVisible(), "The button should be hidden for javascript: links");
        assertFalse(modelData.isButtonVisible(), "The button should be hidden for data: links");
        assertFalse(modelVbscript.isButtonVisible(), "The button should be hidden for vbscript: links");
    }

    @Test
    void shouldBeEmptyWhenNoContentIsProvided() {
        MainBannerModel model = createModel();
        assertNotNull(model, "The model should not be null when created");
        assertTrue(model.isEmpty(), "The model should be considered empty when no relevant content is provided");
    }

    @Test
    void shouldNotBeEmptyWhenImageIsProvided() {
        MainBannerModel model = createModel(
            "fileReference", "/content/dam/brumacafe/main-banner.jpg"
        );

        assertNotNull(model, "The model should not be null when created");
        assertFalse(model.isEmpty(), "The model should not be empty when an image is provided");
    }

    private MainBannerModel createModel(Object... properties) {
        Resource resource = context.create().resource(
            "/content/main-banner-" + UUID.randomUUID().toString(),
            mergeWithResourceType(properties)
        );

        return resource.adaptTo(MainBannerModel.class);
    }

    private Object[] mergeWithResourceType(Object... properties) {
        Object[] result = new Object[properties.length + 2];

        result[0] = "sling:resourceType";
        result[1] = MainBannerModel.RESOURCE_TYPE;

        System.arraycopy(
            properties,
            0,
            result,
            2,
            properties.length
        );

        return result;
    }
}