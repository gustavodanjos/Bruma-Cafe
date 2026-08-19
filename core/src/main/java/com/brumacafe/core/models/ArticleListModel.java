package com.brumacafe.core.models;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArticleListModel {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleListModel.class);

    @ValueMapValue
    private String listRoot;

    @ValueMapValue
    @Default(intValues = 4)
    private int limit;

    @ValueMapValue
    @Default(values = "desc")
    private String sortOrder;

    @OSGiService
    private QueryBuilder queryBuilder;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<ArticleDTO> articles = new ArrayList<>();

    @PostConstruct
    protected void init() {
        if (StringUtils.isBlank(listRoot)) {
            LOG.warn("ListRoot não configurado no componente de Listagem de Artigos.");
            return;
        }

        Session session = resourceResolver.adaptTo(Session.class);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (session == null || pageManager == null) {
            return;
        }

        try {
            Map<String, String> map = new HashMap<>();
            map.put("path", listRoot);
            map.put("type", "cq:Page");
            map.put("property", "jcr:content/cq:template");
            map.put("property.value", "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo");
            map.put("orderby", "@jcr:content/cq:lastModified");
            map.put("orderby.sort", sortOrder);
            map.put("p.limit", String.valueOf(limit));

            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                Page page = pageManager.getPage(hit.getPath());
                if (page != null) {
                    String imagePath = extractCoverImage(page);

                    articles.add(new ArticleDTO(
                            page.getTitle() != null ? page.getTitle() : page.getName(),
                            page.getDescription(),
                            page.getPath() + ".html",
                            page.getLastModified() != null ? page.getLastModified().getTime() : null,
                            imagePath
                    ));
                }
            }
        } catch (Exception e) {
            LOG.error("Erro ao buscar artigos no QueryBuilder", e);
        }
    }

   
    private String extractCoverImage(Page page) {
        Resource contentResource = page.getContentResource();
        if (contentResource == null) return null;

        Resource featuredImage = contentResource.getChild("cq:featuredimage");
        if (featuredImage != null) {
            return featuredImage.getValueMap().get("fileReference", String.class);
        }

        Resource imageNode = contentResource.getChild("image");
        if (imageNode != null) {
            return imageNode.getValueMap().get("fileReference", String.class);
        }
        
        return null;
    }

    public List<ArticleDTO> getArticles() {
        return articles;
    }

    public boolean isEmpty() {
        return articles.isEmpty();
    }
}