package com.amcart.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class CreateProductRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;

    @DecimalMin("0.00")
    private BigDecimal salePrice;

    private UUID brandId;

    private UUID categoryId;

    private boolean featured;

    private boolean newArrival;

    private Set<String> tags;
}
