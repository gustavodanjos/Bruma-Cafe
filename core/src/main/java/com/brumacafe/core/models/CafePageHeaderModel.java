package com.brumacafe.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CafePageHeaderModel {

    @ValueMapValue
    private String chapeu;

    @ValueMapValue
    private String titulo;

    @ValueMapValue
    private String descricao;

    public String getChapeu() {
        return chapeu;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isConfigured() {
        return titulo != null && !titulo.trim().isEmpty();
    }
}