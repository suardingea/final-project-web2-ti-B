package com.example.productcrud.controller;

// Thomas(2481011) mengerjakan bagian User Profil

import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final String uploadDir = "uploads/";

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        model.addAttribute("user", user);
        return "profile/view";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute User updatedUser,
                                @RequestParam("photoFile") MultipartFile photoFile,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        user.setFullName(updatedUser.getFullName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAddress(updatedUser.getAddress());
        user.setBio(updatedUser.getBio());

        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                String originalFilename = photoFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFilename = UUID.randomUUID().toString() + extension;

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(newFilename);
                Files.write(filePath, photoFile.getBytes());

                user.setProfileImageUrl("/uploads/" + newFilename);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Gagal upload foto!");
                return "redirect:/profile/edit";
            }
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Profil berhasil diperbarui!");
        return "redirect:/profile";
    }
}