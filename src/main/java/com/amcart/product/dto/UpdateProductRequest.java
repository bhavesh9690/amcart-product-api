package com.amcart.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class UpdateProductRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @DecimalMin("0.00")
    private BigDecimal price;

    @DecimalMin("0.00")
    private BigDecimal salePrice;

    private UUID brandId;

    private UUID categoryId;

    private Boolean featured;

    private Boolean newArrival;

    private Boolean active;

    private Set<String> tags;
}
