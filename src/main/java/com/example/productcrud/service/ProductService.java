package com.example.productcrud.service;

//Nama : Suardin Gea
//Nim   : 2481006
//Mengerjakan Bagian Category Entity + CRUD, Search & Filter, Dashboard //
//Frida Menambahkan fitur Pagination//

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAllByOwner(User owner) {
        return productRepository.findByOwner(owner);
    }

    public Optional<Product> findByIdAndOwner(Long id, User owner) {
        return productRepository.findByIdAndOwner(id, owner);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteByIdAndOwner(Long id, User owner) {
        productRepository.findByIdAndOwner(id, owner)
                .ifPresent(product -> productRepository.delete(product));
    }

    public long countByOwner(User owner) {
        return productRepository.findByOwner(owner).size();
    }

    public long sumInventoryValueByOwner(User owner) {
        return productRepository.sumInventoryValueByOwner(owner);
    }

    public long countActiveByOwner(User owner) {
        return productRepository.countByOwnerAndActive(owner, true);
    }

    public long countInactiveByOwner(User owner) {
        return productRepository.countByOwnerAndActive(owner, false);
    }

    public List<Product> findLowStockByOwner(User owner) {
        return productRepository.findByOwnerAndStockLessThan(owner, 5);
    }

    public Map<String, Long> countByCategory(User owner) {
        return productRepository.findByOwner(owner).stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getName(),
                        Collectors.counting()
                ));
    }


    public Page<Product> search(User owner, String keyword, Category category, int page) {
        String kw = (keyword != null && keyword.isBlank()) ? null : keyword;
        Pageable pageable = PageRequest.of(page, 10);
        return productRepository.searchByOwner(owner, kw, category, pageable);
    }
}