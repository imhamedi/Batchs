package com.batch.poc.processor;

import org.springframework.batch.item.ItemProcessor;

import com.batch.poc.domain.OSProduct;
import com.batch.poc.domain.Product;

public class MyProductItemProcessor implements ItemProcessor<Product, OSProduct> {

    @Override
    public OSProduct process(Product item) throws Exception {
        System.out.println("processor() executed");
        OSProduct osProduct = new OSProduct();
        osProduct.setProductId(item.getProductId());
        osProduct.setProductName(item.getProductName());
        osProduct.setProductCategory(item.getProductCategory());
        osProduct.setProductPrice(item.getProductPrice());
        osProduct.setTaxPercent(item.getProductCategory().equals("Sports Accessories") ? 5 : 18);
        osProduct.setSku(item.getProductCategory().substring(0, 3) + item.getProductId());
        osProduct.setShippingRate(item.getProductPrice() < 1000 ? 75 : 0);
        return osProduct;
    }

}
