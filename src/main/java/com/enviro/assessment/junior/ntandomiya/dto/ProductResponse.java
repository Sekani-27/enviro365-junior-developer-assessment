package com.enviro.assessment.junior.ntandomiya.dto;

import java.math.BigDecimal;

/**
 * Defines the product information exposed through the API.
 *
 * DTOs prevent database entities from being returned directly to clients.
 * This keeps the API response independent from the internal database model.
 */
public class ProductResponse {

    private Long id;
    private String name;
    private String type;
    private BigDecimal productValue;

    public ProductResponse(
            Long id,
            String name,
            String type,
            BigDecimal productValue) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.productValue = productValue;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getProductValue() {
        return productValue;
    }
}