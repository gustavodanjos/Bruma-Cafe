package com.brumacafe.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentElement;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CafeModelTest {

    public final AemContext context = new AemContext();

    @Mock
    private ContentFragment cafeFragment;

    @Mock
    private ContentFragment produtorFragment;
    
    @Mock
    private ContentElement nomeCafeElement;
    
    @Mock
    private ContentElement origemCafeElement;
    
    @Mock
    private ContentElement fotoCafeElement;
    
    @Mock
    private ContentElement produtorRefElement;
    
    @Mock
    private ContentElement produtorNomeElement;

    private CafeModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(CafeModel.class);

        // Register adapter for Resource to ContentFragment
        context.registerAdapter(Resource.class, ContentFragment.class, (Function<Resource, ContentFragment>) resource -> {
            if (resource.getPath().equals("/content/dam/cafes/cafe1")) return cafeFragment;
            if (resource.getPath().equals("/content/dam/produtores/prod1")) return produtorFragment;
            return null;
        });
    }

    @Test
    void testHappyPath() {
        // Mock Cafe Fragment
        when(cafeFragment.hasElement("nomeCafe")).thenReturn(true);
        when(cafeFragment.getElement("nomeCafe")).thenReturn(nomeCafeElement);
        when(nomeCafeElement.getContent()).thenReturn("Bruma Especial");

        when(cafeFragment.hasElement("origemCafe")).thenReturn(true);
        when(cafeFragment.getElement("origemCafe")).thenReturn(origemCafeElement);
        when(origemCafeElement.getContent()).thenReturn("Sul de Minas");

        when(cafeFragment.hasElement("fotoCafe")).thenReturn(true);
        when(cafeFragment.getElement("fotoCafe")).thenReturn(fotoCafeElement);
        when(fotoCafeElement.getContent()).thenReturn("/content/dam/brumacafe/cafe1.jpg");

        when(cafeFragment.hasElement("produtorRef")).thenReturn(true);
        when(cafeFragment.getElement("produtorRef")).thenReturn(produtorRefElement);
        when(produtorRefElement.getContent()).thenReturn("/content/dam/produtores/prod1");

        // Mock Produtor Fragment
        context.create().resource("/content/dam/produtores/prod1");
        when(produtorFragment.hasElement("nome")).thenReturn(true);
        when(produtorFragment.getElement("nome")).thenReturn(produtorNomeElement);
        when(produtorNomeElement.getContent()).thenReturn("Fazenda Alegria");

        // Create virtual nodes
        context.create().resource("/content/dam/cafes/cafe1");
        
        Resource component = context.create().resource("/content/cafe-component");
        Map<String, Object> itemProps = new HashMap<>();
        itemProps.put("fragmentPath", "/content/dam/cafes/cafe1");
        context.create().resource("/content/cafe-component/cafes/item0", itemProps);
        
        context.currentResource(component);

        model = context.request().adaptTo(CafeModel.class);

        assertNotNull(model);
        assertTrue(model.isConfigured());
        assertEquals(1, model.getListaDeCafes().size());
        
        CafeModel.CafeItem item = model.getListaDeCafes().get(0);
        assertEquals("Bruma Especial", item.getNome());
        assertEquals("Sul de Minas", item.getDescricao());
        assertEquals("/content/dam/brumacafe/cafe1.jpg", item.getImagem());
        assertEquals("Fazenda Alegria", item.getProdutor());
    }
}
