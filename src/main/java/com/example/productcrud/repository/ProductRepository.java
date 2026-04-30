package com.example.productcrud.repository;

//Nama : Suardin Gea
//Nim   : 2481006
//Mengerjakan Bagian Category Entity + CRUD, Search & Filter, Dashboard //


import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByOwner(User owner);

    Optional<Product> findByIdAndOwner(Long id, User owner);

    @Query("SELECT COALESCE(SUM(p.price * p.stock), 0) FROM Product p WHERE p.owner = :owner")
    long sumInventoryValueByOwner(@Param("owner") User owner);

    long countByOwnerAndActive(User owner, boolean active);

    List<Product> findByOwnerAndStockLessThan(User owner, int stock);

    // Search & Filter (✅ sudah difix CAST)
    @Query("SELECT p FROM Product p WHERE p.owner = :owner " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
            "AND (:category IS NULL OR p.category = :category)")
    List<Product> searchByOwner(@Param("owner") User owner,
                                @Param("keyword") String keyword,
                                @Param("category") Category category);
}