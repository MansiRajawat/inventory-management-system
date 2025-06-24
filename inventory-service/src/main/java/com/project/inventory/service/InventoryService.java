package com.project.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory.Utility.DocUtility;
import com.project.inventory.dao.ProductRepository;
import com.project.inventory.exception.ProductIdAlreadyMappedException;
import com.project.inventory.exception.ResourceNotFoundException;
import com.project.inventory.model.Details;
import com.project.inventory.model.FileResponse;
import com.project.inventory.model.ProductDetails;
import com.project.inventory.model.ProductResponse;
import com.project.inventory.serviceImpl.InventoryServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class InventoryService implements InventoryServiceImpl {

    @Autowired
    private ProductRepository productDao;

    @Autowired
    private DocUtility utility;


    @Override
    public List<ProductResponse> saveListOfProductDetails(Details details) {
        if (CollectionUtils.isEmpty(details.getProductDetails()) ) {
            throw new IllegalArgumentException("Product details list cannot be null or empty");
        }
        List<ProductDetails> productDetailsList = new ArrayList<>();

        for (ProductDetails product : details.getProductDetails()) {
            Optional<ProductDetails> checkProductExist = productDao.findById(product.getProductId());

            if (checkProductExist.isPresent()) {
                ProductDetails existingProduct = checkProductExist.get();

                if (!existingProduct.getProductName().equalsIgnoreCase(product.getProductName())) {
                    throw new ProductIdAlreadyMappedException("Product ID " + product.getProductId()
                            + " is already mapped to '" + existingProduct.getProductName() + "'");
                }
            }

            productDetailsList.add(product);
        }
        List<ProductDetails> saveProducts =  productDao.saveAll(productDetailsList);

        return saveProducts.stream().map(this :: mapProductDetails).collect(Collectors.toList());
    }

    private ProductResponse mapProductDetails(ProductDetails productDetails) {
        ProductResponse mappedDetails = new ProductResponse();
        mappedDetails.setProductId(productDetails.getProductId());
        mappedDetails.setProductName(productDetails.getProductName());
        mappedDetails.setProductPrice(productDetails.getProductPrice());
        mappedDetails.setProductCount(productDetails.getProductCount());
        return mappedDetails;
    }

    @Override
    public FileResponse retriveListOfProducts(String type) {

        /* commenting this code for functional coding approach */
        //   List<ProductDetails> productDetails =  productDao.findAll();

       // List<Details> detailsList = new ArrayList<>()
        // if(CollectionUtils.isNotEmpty(productDetails)) {
//            Details details = new Details();
//            details.setProductDetails(productDetails);
//            return details;
//        }
 //       return new Details();

        List<ProductDetails> products = productDao.findAll();

        if("pdf".equalsIgnoreCase(type)){
            return FileResponse.builder()
                    .data(utility.generatePdf(products))
                    .fileName("products.pdf")
                    .mediaType(MediaType.APPLICATION_PDF)
                    .build();
        } else if("excel".equalsIgnoreCase(type)){
            byte[] getBytes = utility.generateExcel(products);
            return getInventoryResponseAsExcel(getBytes);
        }
        try {
            Details details = new Details();
            details.setProductDetails(products);
            return FileResponse.builder()
                    .data(new ObjectMapper().writeValueAsBytes(details))
                    .mediaType(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    private static FileResponse getInventoryResponseAsExcel(byte[] excelBytes) {
        storeExcelToDirectory(excelBytes);
        try {
            return FileResponse.builder()
                    .data("excel generated".getBytes("UTF-8"))
                    .mediaType(MediaType.TEXT_PLAIN)
                    .build();
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 not supported", ex);
        }
    }

    private static void storeExcelToDirectory(byte[] excelBytes) {
        File targetFile = new File("{directory}/products.xlsx");
        File parentDir = targetFile.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            fos.write(excelBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Excel to disk", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public Optional<ProductDetails> getProductById(Long productId) {
        return productDao.findById(productId);
    }

    @Override
    public Optional<ProductDetails> deleteProduct(Long id) {
        Optional<ProductDetails> productOpt = getProductById(id);
        productOpt.ifPresent(productDao::delete);
        return productOpt;

    }

    @Override
    public ProductDetails updateProductDetails(Long id , ProductDetails productDetails) {
        ProductDetails existingDetails = productDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product not found"));
        existingDetails.setProductName(productDetails.getProductName());
        existingDetails.setProductCount(productDetails.getProductCount());
        existingDetails.setProductPrice(productDetails.getProductPrice());
        return productDao.save(existingDetails);
    }

    @Override
    public Optional<ProductDetails> restoreProductDetails(Long id, int quantity) {
        return productDao.findById(id).map(product -> {
            product.setProductCount(product.getProductCount() + quantity);
            return productDao.save(product);
        });
    }

    @Override
    public List<ProductDetails> getLimitedProductDetails(Integer limit, Integer page) {
        int offset = (page - 1) * limit;
        List<ProductDetails> getData = productDao.retrieveProducts(limit, offset);
        if (getData != null) {
            return getData;
        }

        return Collections.emptyList();
    }
}

