package com.batch.poc.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class ProductValidator implements Validator<Product> {
    // sera remplacé par les validateurs jakarta bean validation dans Product.java
    // @Pattern(regexp =
    // "Mobile
    // Phones|Tablets|Televisions|Sports Accessories")
    // et @Max(100000)
    List<String> validProductCategories = Arrays.asList("Mobile Phones", "Tablets", "Televisions",
            "Sports Accessories");

    @Override
    public void validate(Product value) throws ValidationException {
        if (!validProductCategories.contains(value.getProductCategory())) {
            throw new ValidationException("Invalid Product Category");
        }
        if (value.getProductPrice() > 100000) {
            throw new ValidationException("Invalid Product Price");
        }
    }

}
