package com.example.productcrud.repository;

//Nama : Suardin Gea
//Nim   : 2481006
//Mengerjakan Bagian Category Entity + CRUD, Search & Filter, Dashboard //


import com.example.productcrud.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    boolean existsByNameAndUserId(String name, Long userId);
}