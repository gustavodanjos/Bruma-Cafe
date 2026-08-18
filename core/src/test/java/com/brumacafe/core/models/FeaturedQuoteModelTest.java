package com.brumacafe.core.models;

import com.adobe.cq.wcm.core.components.models.Text;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class FeaturedQuoteModelTest {

    private static final String CORE_TEXT_RESOURCE_TYPE =
            "core/wcm/components/text/v2/text";

    public final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Registers the model under test and a lightweight Core Text delegate
        // used only by the unit tests.
        context.addModelsForClasses(
                FeaturedQuoteModel.class,
                CoreTextDelegateStub.class
        );

        // Simulates the proxy component definition in /apps.
        context.create().resource(
                "/apps/brumacafe/components/featured-quote",
                "sling:resourceSuperType", CORE_TEXT_RESOURCE_TYPE
        );
    }

    @Test
    void testHappyPath_DelegatesCoreTextAndReturnsAuthor() {
        // 1. ARRANGE: Create a Featured Quote using the Core Text properties
        context.create().resource(
                "/content/featured-quote",
                "sling:resourceType", FeaturedQuoteModel.RESOURCE_TYPE,
                "text", "<p>Selecionamos <strong>cafés especiais</strong>.</p>",
                "textIsRich", true,
                "author", "Bruma Café"
        );

        context.currentResource("/content/featured-quote");

        // 2. ACT: Adapt the mocked request to the FeaturedQuoteModel
        FeaturedQuoteModel model =
                context.request().adaptTo(FeaturedQuoteModel.class);

        // 3. ASSERT: Verify Core Text delegation and the custom author field
        assertNotNull(model, "The FeaturedQuoteModel should not be null");

        assertEquals(
                "<p>Selecionamos <strong>cafés especiais</strong>.</p>",
                model.getText(),
                "The text should be provided by the Core Text delegate"
        );

        assertTrue(
                model.isRichText(),
                "Rich text information should be provided by the Core Text delegate"
        );

        assertEquals(
                "Bruma Café",
                model.getAuthor(),
                "The author should match the configured value"
        );

        assertFalse(
                model.isEmpty(),
                "A quote containing text should not be empty"
        );
    }

    @Test
    void testOptionalAuthor_QuoteRemainsValidWithoutAuthor() {
        // 1. ARRANGE: Create a quote without the optional author property
        context.create().resource(
                "/content/featured-quote",
                "sling:resourceType", FeaturedQuoteModel.RESOURCE_TYPE,
                "text", "O café especial começa muito antes da xícara.",
                "textIsRich", false
        );

        context.currentResource("/content/featured-quote");

        // 2. ACT
        FeaturedQuoteModel model =
                context.request().adaptTo(FeaturedQuoteModel.class);

        // 3. ASSERT
        assertNotNull(model, "The FeaturedQuoteModel should not be null");

        assertEquals(
                "O café especial começa muito antes da xícara.",
                model.getText()
        );

        assertFalse(model.isRichText());

        assertNull(
                model.getAuthor(),
                "The author should be null when it is not configured"
        );

        assertFalse(
                model.isEmpty(),
                "The quote should remain valid without an author"
        );
    }

    @Test
    void testFailurePath_EmptyTextIsEmpty() {
        // 1. ARRANGE: Create the component without Core Text content
        context.create().resource(
                "/content/featured-quote",
                "sling:resourceType", FeaturedQuoteModel.RESOURCE_TYPE
        );

        context.currentResource("/content/featured-quote");

        // 2. ACT
        FeaturedQuoteModel model =
                context.request().adaptTo(FeaturedQuoteModel.class);

        // 3. ASSERT
        assertNotNull(model, "The FeaturedQuoteModel should not be null");

        assertNull(model.getText());

        assertTrue(
                model.isEmpty(),
                "The component should be empty when no quote text is configured"
        );
    }

    /**
     * Lightweight Core Text implementation used only to test the
     * ResourceSuperType delegation performed by FeaturedQuoteModel.
     */
    @Model(
            adaptables = SlingHttpServletRequest.class,
            adapters = Text.class,
            resourceType = CORE_TEXT_RESOURCE_TYPE,
            defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public static class CoreTextDelegateStub implements Text {

        @ValueMapValue
        private String text;

        @ValueMapValue(name = "textIsRich")
        private boolean richText;

        @Override
        public String getText() {
            return text;
        }

        @Override
        public boolean isRichText() {
            return richText;
        }
    }
}