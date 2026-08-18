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
import java.util.List;

@Component(service = StoreService.class, immediate = true)
@Designate(ocd = StoreApiConfig.class)
public class StoreServiceImpl implements StoreService {

    private static final Logger LOG = LoggerFactory.getLogger(StoreServiceImpl.class);

    private String apiUrl;
    private int productLimit;
    private int timeout;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Activate
    @Modified
    protected void activate(StoreApiConfig config) {
        this.apiUrl = config.apiUrl();
        this.productLimit = config.productLimit();
        this.timeout = config.connectionTimeout();
    }

    @Override
    public List<ProductDto> getProducts() {
        List<ProductDto> products = new ArrayList<>();
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
                        products = responseDto.getProducts();
                    }
                }
            } else {
                LOG.error("Falha na API da loja. Status: {}", statusCode);
            }
        } catch (Exception e) {
            LOG.error("Erro ao consultar API externa: ", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return products;
    }
}