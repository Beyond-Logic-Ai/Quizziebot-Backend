package com.quizzka.backend.service;

import com.quizzka.backend.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> getAllCategories();
    Optional<Category> getCategoryByName(String name);
    Category saveCategory(Category category);
}
