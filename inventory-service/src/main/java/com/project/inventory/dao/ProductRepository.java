package com.project.inventory.dao;

import com.project.inventory.model.Details;
import com.project.inventory.model.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductDetails, Long> {

    /* procedure to create a pagination to show details of let's say 5 orders in each page */
    @Query(nativeQuery = true,value = "{CALL RETRIEVE_PRODUCTS(:limit_val, :offset_val)};")
    public List<ProductDetails> retrieveProducts(@Param("limit_val") int limit, @Param("offset_val") int offset);

}
