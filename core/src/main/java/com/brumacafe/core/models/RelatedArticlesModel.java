package com.brumacafe.core.models;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
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
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class RelatedArticlesModel {

    private static final Logger LOG = LoggerFactory.getLogger(RelatedArticlesModel.class);

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private QueryBuilder queryBuilder;

    @ValueMapValue
    @Default(values = "Leia também")
    private String title;

    @ValueMapValue
    private String listRoot;

    @ValueMapValue
    @Default(intValues = 3)
    private int limit;

    private List<ArticleDTO> articles = new ArrayList<>();

    @PostConstruct
    protected void init() {
        if (currentPage == null || resourceResolver == null || queryBuilder == null) {
            return;
        }

        Session session = resourceResolver.adaptTo(Session.class);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (session == null || pageManager == null) {
            return;
        }

        Set<String> currentTagIds = extractPageTagIds(currentPage);
        if (currentTagIds.isEmpty()) {
            return;
        }

        String searchPath = listRoot;
        if (StringUtils.isBlank(searchPath)) {
            Page parent = currentPage.getParent();
            searchPath = parent != null ? parent.getPath() : "/content/brumacafe/br/pt/hub";
        }

        try {
            Map<String, String> map = new HashMap<>();
            map.put("path", searchPath);
            map.put("type", "cq:Page");
            map.put("property", "jcr:content/cq:template");
            map.put("property.value", "/conf/brumacafe/settings/wcm/templates/pagina-de-artigo");
            map.put("orderby", "@jcr:content/cq:lastModified");
            map.put("orderby.sort", "desc");
            map.put("p.limit", "20");

            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();

            Set<String> processedNames = new HashSet<>();

            for (Hit hit : result.getHits()) {
                if (articles.size() >= limit) {
                    break;
                }

                Page candidatePage = pageManager.getPage(hit.getPath());
                if (candidatePage == null) {
                    continue;
                }

                if (candidatePage.getPath().equals(currentPage.getPath())
                        || candidatePage.getName().equalsIgnoreCase(currentPage.getName())) {
                    continue;
                }

                if (processedNames.contains(candidatePage.getName())) {
                    continue;
                }

                if (sharesTag(candidatePage, currentTagIds)) {
                    String imagePath = extractCoverImage(candidatePage);
                    String category = extractCategory(candidatePage);

                    ArticleInfoModel infoModel = null;
                    if (candidatePage.getContentResource() != null) {
                        infoModel = candidatePage.getContentResource().adaptTo(ArticleInfoModel.class);
                    }

                    String formattedDate = infoModel != null ? infoModel.getFormattedDate() : "";
                    int readingTime = infoModel != null ? infoModel.getReadingTime() : 1;

                    String articleTitle = StringUtils.defaultIfBlank(candidatePage.getTitle(), candidatePage.getName());

                    articles.add(new ArticleDTO(
                            articleTitle,
                            candidatePage.getDescription(),
                            candidatePage.getPath() + ".html",
                            candidatePage.getLastModified() != null ? candidatePage.getLastModified().getTime() : null,
                            imagePath,
                            category,
                            formattedDate,
                            readingTime
                    ));
                    processedNames.add(candidatePage.getName());
                }
            }

        } catch (Exception e) {
            LOG.error("Erro ao processar artigos relacionados", e);
        }
    }

    private Set<String> extractPageTagIds(Page page) {
        Tag[] tags = page.getTags();
        if (tags == null || tags.length == 0) {
            return Collections.emptySet();
        }

        Set<String> tagIds = new HashSet<>();
        for (Tag tag : tags) {
            tagIds.add(tag.getTagID());
            tagIds.add(tag.getName());
        }
        return tagIds;
    }

    private boolean sharesTag(Page page, Set<String> targetTagIds) {
        Tag[] tags = page.getTags();
        if (tags == null || tags.length == 0) {
            return false;
        }

        for (Tag tag : tags) {
            if (targetTagIds.contains(tag.getTagID()) || targetTagIds.contains(tag.getName())) {
                return true;
            }
        }
        return false;
    }

    private String extractCategory(Page page) {
        Tag[] tags = page.getTags();
        if (tags != null && tags.length > 0) {
            return StringUtils.defaultIfBlank(tags[0].getTitle(), tags[0].getName());
        }
        return "Conteúdo";
    }

    private String extractCoverImage(Page page) {
        Resource contentResource = page.getContentResource();
        if (contentResource == null) return null;

        return findImageFileReference(contentResource, 0, new HashSet<>());
    }

    private String findImageFileReference(Resource resource, int depth, Set<String> visited) {
        final int MAX_DEPTH = 5;

        if (resource == null || depth > MAX_DEPTH) {
            return null;
        }

        String path = resource.getPath();
        if (visited.contains(path)) {
            return null;
        }
        visited.add(path);

        String fileRef = resource.getValueMap().get("fileReference", String.class);
        if (StringUtils.isNotBlank(fileRef)) {
            return fileRef;
        }

        for (Resource child : resource.getChildren()) {
            String childRef = findImageFileReference(child, depth + 1, visited);
            if (StringUtils.isNotBlank(childRef)) {
                return childRef;
            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public List<ArticleDTO> getArticles() {
        return articles;
    }

    public boolean isEmpty() {
        return articles.isEmpty();
    }
}