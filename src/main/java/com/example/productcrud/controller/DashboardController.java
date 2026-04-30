package com.example.productcrud.controller;

//Nama : Suardin Gea
//Nim   : 2481006
//Mengerjakan Bagian Category Entity + CRUD, Search & Filter, Dashboard //

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public DashboardController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = getCurrentUser(userDetails);

        model.addAttribute("totalProduk", productService.countByOwner(currentUser));
        model.addAttribute("totalInventory", productService.sumInventoryValueByOwner(currentUser));
        model.addAttribute("totalAktif", productService.countActiveByOwner(currentUser));
        model.addAttribute("totalTidakAktif", productService.countInactiveByOwner(currentUser));
        model.addAttribute("lowStockProducts", productService.findLowStockByOwner(currentUser));
        model.addAttribute("produkPerKategori", productService.countByCategory(currentUser));

        return "dashboard";
    }
}