package com.brumacafe.core.models;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CafeModel {

    @ChildResource
    private List<Resource> cafes;

    @ValueMapValue
    private String targetLink;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<CafeItem> listaDeCafes = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (cafes != null && resourceResolver != null) {
            for (Resource itemResource : cafes) {
                String path = itemResource.getValueMap().get("fragmentPath", String.class);
                if (path != null && !path.isEmpty()) {
                    CafeItem item = processarFragmento(path);
                    if (item != null) {
                        listaDeCafes.add(item);
                    }
                }
            }
        }
    }

    private CafeItem processarFragmento(String path) {
        Resource fragmentResource = resourceResolver.getResource(path);
        if (fragmentResource == null) {
            return null;
        }

        ContentFragment cf = fragmentResource.adaptTo(ContentFragment.class);
        if (cf == null) {
            return null;
        }

        CafeItem item = new CafeItem();
        item.setId(fragmentResource.getName());

        if (cf.hasElement("nomeCafe")) {
            item.setNome(cf.getElement("nomeCafe").getContent());
        }
        if (cf.hasElement("origemCafe")) {
            item.setDescricao(cf.getElement("origemCafe").getContent());
        }
        if (cf.hasElement("fotoCafe")) {
            item.setImagem(cf.getElement("fotoCafe").getContent());
        }

        if (cf.hasElement("notasSensoriaisEDescricao")) {
            item.setNotasSensoriaisDescricao(cf.getElement("notasSensoriaisEDescricao").getContent());
        }
        if (cf.hasElement("processo")) {
            item.setProcesso(cf.getElement("processo").getContent());
        }
        if (cf.hasElement("variedade")) {
            item.setVariedade(cf.getElement("variedade").getContent());
        }
        if (cf.hasElement("altitude")) {
            item.setAltitude(cf.getElement("altitude").getContent());
        }
        if (cf.hasElement("safra")) {
            item.setSafra(cf.getElement("safra").getContent());
        }
        if (cf.hasElement("notasBadges")) {
            String notasStr = cf.getElement("notasBadges").getContent();
            if (notasStr != null && !notasStr.trim().isEmpty()) {
                String[] notas = notasStr.split(",");
                List<String> list = new ArrayList<>();
                for(String n : notas) {
                    list.add(n.trim());
                }
                item.setNotasBadges(list);
            }
        }

        if (cf.hasElement("produtorRef")) {
            String produtorPath = cf.getElement("produtorRef").getContent();
            if (produtorPath != null && !produtorPath.isEmpty()) {
                Resource produtorResource = resourceResolver.getResource(produtorPath);
                if (produtorResource != null) {
                    ContentFragment produtorCF = produtorResource.adaptTo(ContentFragment.class);
                    if (produtorCF != null && produtorCF.hasElement("nome")) {
                        item.setProdutor(produtorCF.getElement("nome").getContent());
                    }
                }
            }
        }

        return item;
    }

    public List<CafeItem> getListaDeCafes() {
        return listaDeCafes;
    }

    public boolean isConfigured() {
        return !listaDeCafes.isEmpty();
    }

    public String getTargetLink() {
        return targetLink;
    }

    public boolean isHasTargetLink() {
        return targetLink != null && !targetLink.trim().isEmpty();
    }

    public static class CafeItem {
        private String id;
        private String nome;
        private String descricao;
        private String imagem;
        private String produtor;
        private String notasSensoriaisDescricao;
        private String processo;
        private String variedade;
        private String altitude;
        private String safra;
        private List<String> notasBadges;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getImagem() { return imagem; }
        public void setImagem(String imagem) { this.imagem = imagem; }

        public String getProdutor() { return produtor; }
        public void setProdutor(String produtor) { this.produtor = produtor; }

        public String getNotasSensoriaisDescricao() { return notasSensoriaisDescricao; }
        public void setNotasSensoriaisDescricao(String notasSensoriaisDescricao) { this.notasSensoriaisDescricao = notasSensoriaisDescricao; }

        public String getProcesso() { return processo; }
        public void setProcesso(String processo) { this.processo = processo; }

        public String getVariedade() { return variedade; }
        public void setVariedade(String variedade) { this.variedade = variedade; }

        public String getAltitude() { return altitude; }
        public void setAltitude(String altitude) { this.altitude = altitude; }

        public String getSafra() { return safra; }
        public void setSafra(String safra) { this.safra = safra; }

        public List<String> getNotasBadges() { return notasBadges; }
        public void setNotasBadges(List<String> notasBadges) { this.notasBadges = notasBadges; }
    }
}