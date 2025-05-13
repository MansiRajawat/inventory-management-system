package com.project.inventory.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InventoryResponse {
  private  List<ProductResponse> productResponses;

}
