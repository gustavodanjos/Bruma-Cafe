package com.brumacafe.core.models;

import javax.annotation.PostConstruct;
import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CafeModel {
    
    @ValueMapValue
    @Via("resource")
    private String fragmentPath;

    @SlingObject
    private ResourceResolver resourceResolver;


    private String nome;
    private String descricao;
    private String imagem;
    private String produtor;

    @PostConstruct
    public void init() {
        if (fragmentPath != null && resourceResolver != null) {
            Resource fragmentResource = resourceResolver.getResource(fragmentPath);
            if (fragmentResource != null) {
                ContentFragment contentFragment = fragmentResource.adaptTo(ContentFragment.class);
                
                if (contentFragment != null) {
                    // Validação feita para o caso de futuramente remover obrigatoriedade dos campos
                    if (contentFragment.hasElement("nomeCafe")) {
                        this.nome = contentFragment.getElement("nomeCafe").getContent();
                    }
                    if (contentFragment.hasElement("origemCafe")){
                        this.descricao = contentFragment.getElement("origemCafe").getContent();
                    }
                    if (contentFragment.hasElement("fotoCafe")){
                    this.imagem = contentFragment.getElement("fotoCafe").getContent();
                    }
                    if (contentFragment.hasElement("produtorRef")){
                        Resource produtorfragmentResource = resourceResolver.getResource(contentFragment.getElement("produtorRef").getContent());
                        ContentFragment produtorcontentFragment = produtorfragmentResource.adaptTo(ContentFragment.class);
                        this.produtor=produtorcontentFragment.getElement("nome").getContent();
                    }
                } else {
                    this.nome = fragmentResource.getValueMap().get("nomeCafe", String.class);
                    this.descricao = fragmentResource.getValueMap().get("origemCafe", String.class);
                    this.imagem = fragmentResource.getValueMap().get("fotoCafe", String.class);
                    this.produtor=fragmentResource.getValueMap().get("produtorRef", String.class);
                }
            }
        }
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public String getProdutor() {
        return produtor;
    }

    public boolean isConfigured() {
        return nome != null && descricao != null && imagem != null && fragmentPath != null;
    }
}
