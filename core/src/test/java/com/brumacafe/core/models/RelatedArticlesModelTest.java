package com.brumacafe.core.models;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RelatedArticlesModelTest {

    public final AemContext context = new AemContext();

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit1;

    @Mock
    private Session session;

    private RelatedArticlesModel model;

    @BeforeEach
    void setUp() throws Exception {
        context.addModelsForClasses(RelatedArticlesModel.class);
        context.registerService(QueryBuilder.class, queryBuilder);
        context.registerAdapter(org.apache.sling.api.resource.ResourceResolver.class, Session.class, session);

        context.create().tag("brumacafe:origin/brazil");

        // Setup current page with some tags
        Page currentPage = context.create().page("/content/brumacafe/us/en/current-article", 
            "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo", 
            "jcr:title", "Current Article",
            "cq:tags", new String[]{"brumacafe:origin/brazil"});

        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, context.currentResource());
        bindings.put(SlingBindings.REQUEST, context.request());
        bindings.put("currentPage", currentPage);
        context.request().setAttribute(SlingBindings.class.getName(), bindings);

        // Setup a related page
        Page relatedPage = context.create().page("/content/brumacafe/us/en/related-article", 
            "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo", 
            "jcr:title", "Related Article",
            "cq:tags", new String[]{"brumacafe:origin/brazil"});

        when(queryBuilder.createQuery(any(), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        
        when(hit1.getPath()).thenReturn(relatedPage.getPath());
        when(searchResult.getHits()).thenReturn(Arrays.asList(hit1));
    }

    @Test
    void testHappyPath_RelatedArticlesFound() {
        Map<String, Object> props = new HashMap<>();
        props.put("title", "Related");
        props.put("limit", 2);

        Resource component = context.create().resource("/content/related", props);
        context.currentResource(component);

        model = context.request().adaptTo(RelatedArticlesModel.class);

        assertNotNull(model, "Model should be successfully adapted");
        assertFalse(model.isEmpty(), "Model should not be empty");
        assertEquals("Related", model.getTitle());
        assertEquals(1, model.getArticles().size(), "Should have exactly 1 related article");
        assertEquals("Related Article", model.getArticles().get(0).getTitle());
    }
}
