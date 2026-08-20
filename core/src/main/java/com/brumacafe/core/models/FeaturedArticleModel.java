package com.brumacafe.core.models;

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
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FeaturedArticleModel {

    private static final Logger LOG = LoggerFactory.getLogger(FeaturedArticleModel.class);

    @ValueMapValue
    private String articlePath;

    @ValueMapValue
    @Default(values = "Destaque")
    private String badgeLabel;

    @ValueMapValue
    @Default(values = "Ler artigo completo")
    private String ctaText;

    @SlingObject
    private ResourceResolver resourceResolver;

    private ArticleDTO article;

    @PostConstruct
    protected void init() {
        if (StringUtils.isBlank(articlePath) || resourceResolver == null) {
            return;
        }

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return;
        }

        Page targetPage = pageManager.getPage(articlePath);
        if (targetPage == null) {
            LOG.warn("Artigo destacado não encontrado no caminho: {}", articlePath);
            return;
        }

        try {
            String imagePath = extractCoverImage(targetPage);
            String category = extractCategory(targetPage);

            ArticleInfoModel infoModel = null;
            if (targetPage.getContentResource() != null) {
                infoModel = targetPage.getContentResource().adaptTo(ArticleInfoModel.class);
            }

            String formattedDate = infoModel != null ? infoModel.getFormattedDate() : "";
            int readingTime = infoModel != null ? infoModel.getReadingTime() : 1;

            this.article = new ArticleDTO(
                    targetPage.getTitle() != null ? targetPage.getTitle() : targetPage.getName(),
                    targetPage.getDescription(),
                    targetPage.getPath() + ".html",
                    targetPage.getLastModified() != null ? targetPage.getLastModified().getTime() : null,
                    imagePath,
                    category,
                    formattedDate,
                    readingTime
            );
        } catch (Exception e) {
            LOG.error("Erro ao processar dados do artigo em destaque", e);
        }
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

    public ArticleDTO getArticle() {
        return article;
    }

    public String getBadgeLabel() {
        return badgeLabel;
    }

    public String getCtaText() {
        return ctaText;
    }

    public boolean isEmpty() {
        return article == null;
    }
}