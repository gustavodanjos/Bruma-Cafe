package com.brumacafe.core.models;

import com.brumacafe.core.models.dto.ProductDto;
import com.brumacafe.core.services.StoreService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ShowExternalProductModelTest {

    public final AemContext context = new AemContext();

    @Mock
    private StoreService storeService;

    @Mock
    private ProductDto productDto;

    private ShowExternalProductModel model;

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(ShowExternalProductModel.class);
        context.registerService(StoreService.class, storeService);
    }

    @Test
    void testHappyPath_WithProducts() {
        when(storeService.getProducts()).thenReturn(Arrays.asList(productDto));

        context.currentResource(context.create().resource("/content/external-product"));
        model = context.request().adaptTo(ShowExternalProductModel.class);

        assertNotNull(model);
        assertTrue(model.isConfigured());
        assertEquals(1, model.getProdutos().size());
    }

    @Test
    void testNoProducts() {
        when(storeService.getProducts()).thenReturn(Collections.emptyList());

        context.currentResource(context.create().resource("/content/external-product"));
        model = context.request().adaptTo(ShowExternalProductModel.class);

        assertNotNull(model);
        assertFalse(model.isConfigured());
        assertEquals(0, model.getProdutos().size());
    }
}
