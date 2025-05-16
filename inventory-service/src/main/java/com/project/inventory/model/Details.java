package com.project.inventory.model;


import lombok.Data;

import java.util.List;

    @Data // removing the boiler plate code
    public class Details {
        private List<ProductDetails> productDetails;
    }
