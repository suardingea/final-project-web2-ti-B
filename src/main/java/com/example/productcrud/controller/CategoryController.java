package com.example.productcrud.controller;

//Nama : Suardin Gea
//Nim   : 2481006
//Mengerjakan Bagian Category Entity + CRUD, Search & Filter, Dashboard //

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    // LIST
    @GetMapping
    public String list(Model model, Principal principal) {
        User user = getCurrentUser(principal);
        List<Category> categories = categoryService.getCategoriesByUser(user.getId());
        model.addAttribute("categories", categories);
        return "category/list";
    }

    // FORM TAMBAH
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
    }

    // PROSES TAMBAH
    @PostMapping("/add")
    public String add(@ModelAttribute Category category, Principal principal) {
        category.setUser(getCurrentUser(principal));
        categoryService.save(category);
        return "redirect:/categories";
    }

    // FORM EDIT
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "category/form";
    }

    // PROSES EDIT
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Category category, Principal principal) {
        category.setId(id);
        category.setUser(getCurrentUser(principal));
        categoryService.save(category);
        return "redirect:/categories";
    }

    // HAPUS
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}