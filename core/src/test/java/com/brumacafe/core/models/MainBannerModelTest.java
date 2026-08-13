package com.brumacafe.core.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class MainBannerModelTest {

    private final AemContext context = new AemContext();

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

        assertNotNull(model);
        assertTrue(model.isButtonVisible());
    }

    @Test
    void shouldHideButtonWhenLinkIsMissing() {
        MainBannerModel model = createModel(
            "buttonText", "Explore our coffees"
        );

        assertNotNull(model);
        assertFalse(model.isButtonVisible());
    }

    @Test
    void shouldHideButtonWhenTextIsMissing() {
        MainBannerModel model = createModel(
            "buttonLink", "/content/brumacafe/us/en"
        );

        assertNotNull(model);
        assertFalse(model.isButtonVisible());
    }

    @Test
    void shouldHideButtonWhenLinkIsBlank() {
        MainBannerModel model = createModel(
            "buttonText", "Explore our coffees",
            "buttonLink", "   "
        );

        assertNotNull(model);
        assertFalse(model.isButtonVisible());
    }

    @Test
    void shouldBeEmptyWhenNoContentIsProvided() {
        MainBannerModel model = createModel();

        assertNotNull(model);
        assertTrue(model.isEmpty());
    }

    @Test
    void shouldNotBeEmptyWhenImageIsProvided() {
        MainBannerModel model = createModel(
            "fileReference", "/content/dam/brumacafe/main-banner.jpg"
        );

        assertNotNull(model);
        assertFalse(model.isEmpty());
    }

    private MainBannerModel createModel(Object... properties) {
        Resource resource = context.create().resource(
            "/content/main-banner",
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