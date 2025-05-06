package com.project.inventory.serviceImpl;

import com.project.inventory.model.Details;

import java.util.List;

public interface InventoryServiceImpl {
public List<Details> saveListOfProductDetails (Details details);
public List<Details> retriveListOfProducts();
}
