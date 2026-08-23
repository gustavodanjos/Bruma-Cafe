package com.brumacafe.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArticleHeroModel {

    @ScriptVariable
    private Page currentPage;

    private String title;
    private String category;
    private String imagePath;
    private String formattedDate;
    private int readingTime;
    private String excerpt;
    private String hubPath;

    @PostConstruct
    protected void init() {
        if (currentPage == null) {
            return;
        }

        this.title = StringUtils.defaultIfBlank(currentPage.getPageTitle(), currentPage.getTitle());
        if (StringUtils.isBlank(this.title)) {
            this.title = currentPage.getName();
        }

        this.excerpt = currentPage.getDescription();

        Tag[] tags = currentPage.getTags();
        if (tags != null && tags.length > 0) {
            this.category = StringUtils.defaultIfBlank(tags[0].getTitle(), tags[0].getName());
        } else {
            this.category = "Conteúdo";
        }

        this.imagePath = extractCoverImage(currentPage);

        Resource contentResource = currentPage.getContentResource();
        if (contentResource != null) {
            ArticleInfoModel infoModel = contentResource.adaptTo(ArticleInfoModel.class);
            if (infoModel != null) {
                this.formattedDate = infoModel.getFormattedDate();
                this.readingTime = infoModel.getReadingTime();
            }
        }

        Page parentPage = currentPage.getParent();
        if (parentPage != null) {
            this.hubPath = parentPage.getPath() + ".html";
        } else {
            this.hubPath = "/content/brumacafe/br/pt/hub.html";
        }
    }

    private String extractCoverImage(Page page) {
        Resource contentResource = page.getContentResource();
        if (contentResource == null) return null;
        return findImageFileReference(contentResource, 0, new HashSet<>());
    }

    private String findImageFileReference(Resource resource, int depth, Set<String> visited) {
        final int MAX_DEPTH = 5;
        if (resource == null || depth > MAX_DEPTH) return null;

        String path = resource.getPath();
        if (visited.contains(path)) return null;
        visited.add(path);

        String fileRef = resource.getValueMap().get("fileReference", String.class);
        if (StringUtils.isNotBlank(fileRef)) return fileRef;

        for (Resource child : resource.getChildren()) {
            String childRef = findImageFileReference(child, depth + 1, visited);
            if (StringUtils.isNotBlank(childRef)) return childRef;
        }
        return null;
    }

    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }
    public String getFormattedDate() { return formattedDate; }
    public int getReadingTime() { return readingTime; }
    public String getExcerpt() { return excerpt; }
    public String getHubPath() { return hubPath; }
}