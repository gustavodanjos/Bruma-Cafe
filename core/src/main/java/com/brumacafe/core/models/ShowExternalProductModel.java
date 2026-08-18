package com.brumacafe.core.models;

import com.brumacafe.core.models.dto.ProductDto;
import com.brumacafe.core.services.StoreService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ShowExternalProductModel {

    @OSGiService
    private StoreService storeService;

    private List<ProductDto> produtos = new ArrayList<>();

    @PostConstruct
    protected void init() {
        if (storeService != null) {
            this.produtos = storeService.getProducts();
        }
    }

    public List<ProductDto> getProdutos() {
        return produtos;
    }

    public boolean isConfigured() {
        return produtos != null && !produtos.isEmpty();
    }
}