package com.brumacafe.core.models;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
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
class ArticleListModelTest {

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
    private Hit hit2;

    @Mock
    private Session session;

    private ArticleListModel model;

    @BeforeEach
    void setUp() throws Exception {
        context.addModelsForClasses(ArticleListModel.class);
        context.registerService(QueryBuilder.class, queryBuilder);
        context.registerAdapter(org.apache.sling.api.resource.ResourceResolver.class, Session.class, session);

        context.create().tag("brumacafe:category/Origem");
        context.create().tag("brumacafe:category/Preparo");

        Page article1 = context.create().page("/content/brumacafe/us/en/article1", 
            "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo", 
            "jcr:title", "Origin Brazil",
            "cq:tags", new String[]{"brumacafe:category/Origem"});

        Page article2 = context.create().page("/content/brumacafe/us/en/article2", 
            "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo", 
            "jcr:title", "Brewing Method",
            "cq:tags", new String[]{"brumacafe:category/Preparo"});

        when(queryBuilder.createQuery(any(), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        
        when(hit1.getPath()).thenReturn(article1.getPath());
        when(hit2.getPath()).thenReturn(article2.getPath());
        when(searchResult.getHits()).thenReturn(Arrays.asList(hit1, hit2));
    }

    @Test
    void testHappyPath_ArticlesAndCategories() {
        Map<String, Object> props = new HashMap<>();
        props.put("listRoot", "/content/brumacafe/us/en");
        props.put("limit", 10);
        props.put("sortOrder", "asc");

        Resource component = context.create().resource("/content/articlelist", props);
        context.currentResource(component);

        model = context.request().adaptTo(ArticleListModel.class);

        assertNotNull(model, "Model should be successfully adapted");
        assertFalse(model.isEmpty(), "Model should not be empty");
        
        assertEquals(2, model.getArticles().size(), "Should return 2 articles");
        assertEquals("Origin Brazil", model.getArticles().get(0).getTitle());
        
        // Categories should be ordered: Preparo, Origem
        assertEquals(2, model.getCategories().size(), "Should return 2 unique categories");
        assertEquals("Preparo", model.getCategories().get(0), "Preparo should be first based on ORDERED_CATEGORIES");
        assertEquals("Origem", model.getCategories().get(1));
    }
}
