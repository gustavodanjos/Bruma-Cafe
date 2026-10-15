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

    // Cache em memória baseado na URL
    private static class CacheEntry {
        List<ProductDto> products;
        long timestamp;
        CacheEntry(List<ProductDto> products, long timestamp) {
            this.products = products;
            this.timestamp = timestamp;
        }
    }
    private java.util.Map<String, CacheEntry> cacheMap = new java.util.concurrent.ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Activate
    @Modified
    protected synchronized void activate(StoreApiConfig config) {
        this.apiUrl = config.apiUrl();
        this.productLimit = config.productLimit();
        this.timeout = config.connectionTimeout();
        this.cacheTtlMillis = config.cacheTtlSeconds() * 1000L;

        // Invalida o cache
        this.cacheMap.clear();

        LOG.info("StoreService configurado: URL={}, Limit={}, Timeout={}ms, CacheTTL={}s",
                this.apiUrl, this.productLimit, this.timeout, config.cacheTtlSeconds());
    }

    @Override
    public List<ProductDto> getProducts() {
        return getProducts(this.apiUrl);
    }

    @Override
    public List<ProductDto> getProducts(String customApiUrl) {
        if (customApiUrl == null || customApiUrl.trim().isEmpty()) {
            customApiUrl = this.apiUrl;
        }

        long currentTime = System.currentTimeMillis();
        CacheEntry entry = cacheMap.get(customApiUrl);

        if (entry != null && !entry.products.isEmpty() && (currentTime - entry.timestamp < cacheTtlMillis)) {
            LOG.debug("Retornando produtos do cache em memória para URL: {}", customApiUrl);
            return entry.products;
        }

        LOG.info("Cache expirado ou vazio para {}. Realizando consulta HTTP.", customApiUrl);
        List<ProductDto> fetchedProducts = fetchProductsFromApi(customApiUrl);

        if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
            List<ProductDto> unmodifiable = Collections.unmodifiableList(fetchedProducts);
            cacheMap.put(customApiUrl, new CacheEntry(unmodifiable, currentTime));
            return unmodifiable;
        }

        if (entry != null && !entry.products.isEmpty()) {
            LOG.warn("Falha ao renovar produtos da API. Mantendo cache anterior para {}.", customApiUrl);
            return entry.products;
        }

        return Collections.emptyList();
    }

    private List<ProductDto> fetchProductsFromApi(String targetUrl) {
        HttpURLConnection conn = null;
        try {
            // Se a URL já possui parâmetros (ex: Vercel), cuidamos para não quebrar.
            // Por simplicidade, vou apenas apendar se o user quiser manter o limite.
            // Porém o json do Vercel não tem paginação e traz direto 'products'.
            String fullUrl = targetUrl;
            if (targetUrl.contains("dummyjson")) {
                fullUrl = String.format("%s?limit=%d", targetUrl, this.productLimit);
            }

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