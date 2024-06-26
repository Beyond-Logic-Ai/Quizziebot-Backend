package com.quizzka.backend.controller;

import com.quizzka.backend.entity.Category;
import com.quizzka.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        if (categoryService.getCategoryByName(category.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Category already exists!");
        }

        Category newCategory = categoryService.saveCategory(category);
        return ResponseEntity.ok("Category added successfully with ID: " + newCategory.getId());
    }
}
