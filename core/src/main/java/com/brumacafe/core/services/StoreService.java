package com.brumacafe.core.services;

import java.util.List;
import com.brumacafe.core.models.dto.ProductDto;

public interface StoreService {
    List<ProductDto> getProducts();
}   
