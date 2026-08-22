package com.brumacafe.core.services.impl;

import com.brumacafe.core.config.StoreApiConfig;
import com.brumacafe.core.models.dto.ProductDto;
import com.brumacafe.core.models.dto.ProductResponseDto;
import com.brumacafe.core.services.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component(service = StoreService.class, immediate = true)
@Designate(ocd = StoreApiConfig.class)
public class StoreServiceImpl implements StoreService {

    private static final Logger LOG = LoggerFactory.getLogger(StoreServiceImpl.class);

    private String apiUrl;
    private int productLimit;
    private int timeout;
    private long cacheTtlMillis;

    // Cache em memória
    private List<ProductDto> cachedProducts = Collections.emptyList();
    private long lastCacheTime = 0;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Activate
    @Modified
    protected synchronized void activate(StoreApiConfig config) {
        this.apiUrl = config.apiUrl();
        this.productLimit = config.productLimit();
        this.timeout = config.connectionTimeout();
        this.cacheTtlMillis = config.cacheTtlSeconds() * 1000L;

        // Invalida o cache para recarregar com as novas configs
        this.cachedProducts = Collections.emptyList();
        this.lastCacheTime = 0;

        LOG.info("StoreService configurado: URL={}, Limit={}, Timeout={}ms, CacheTTL={}s",
                this.apiUrl, this.productLimit, this.timeout, config.cacheTtlSeconds());
    }

    @Override
    public synchronized List<ProductDto> getProducts() {
        long currentTime = System.currentTimeMillis();

        if (!cachedProducts.isEmpty() && (currentTime - lastCacheTime < cacheTtlMillis)) {
            LOG.debug("Retornando produtos do cache em memória (idade: {}ms)", currentTime - lastCacheTime);
            return cachedProducts;
        }

        LOG.info("Cache expirado ou vazio. Realizando consulta HTTP na API da loja: {}", this.apiUrl);
        List<ProductDto> fetchedProducts = fetchProductsFromApi();

        if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
            this.cachedProducts = Collections.unmodifiableList(fetchedProducts);
            this.lastCacheTime = currentTime;
            return this.cachedProducts;
        }

        if (!this.cachedProducts.isEmpty()) {
            LOG.warn("Falha ao renovar produtos da API. Mantendo cache anterior.");
            return this.cachedProducts;
        }

        return Collections.emptyList();
    }

    private List<ProductDto> fetchProductsFromApi() {
        HttpURLConnection conn = null;
        try {
            String fullUrl = String.format("%s?limit=%d", this.apiUrl, this.productLimit);
            URL url = new URL(fullUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(this.timeout);
            conn.setReadTimeout(this.timeout);
            conn.setRequestProperty("Accept", "application/json");

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = conn.getInputStream()) {
                    ProductResponseDto responseDto = objectMapper.readValue(inputStream, ProductResponseDto.class);
                    if (responseDto != null && responseDto.getProducts() != null) {
                        return responseDto.getProducts();
                    }
                }
            } else {
                LOG.error("Falha na chamada da API da loja. HTTP Status: {}", statusCode);
            }
        } catch (Exception e) {
            LOG.error("Erro ao consultar API externa da loja: ", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return Collections.emptyList();
    }
}