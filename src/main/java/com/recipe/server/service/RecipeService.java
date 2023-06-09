package com.recipe.server.service;

import com.recipe.server.entity.Category;
import com.recipe.server.entity.Recipe;
import com.recipe.server.exceptions.ResourceNotFoundException;
import com.recipe.server.models.RecipeRequest;
import com.recipe.server.models.RecipeResponse;
import com.recipe.server.repository.CategoryRepository;
import com.recipe.server.repository.RecipeRepository;
import com.recipe.server.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {
    @Autowired
    RecipeRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;


    @CacheEvict(value = "recipes", allEntries = true)
    public RecipeResponse saveRecipe(RecipeRequest recipeRequest) {
        categoryRepository.findById(recipeRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category ID not found", "102"));
        userRepository.findById(Long.valueOf(recipeRequest.getUserId())).orElseThrow(() -> new ResourceNotFoundException("User ID not found", "102"));
        Recipe post = postRepository.save(modelMapper.map(recipeRequest, Recipe.class));
        return modelMapper.map(post, RecipeResponse.class);
    }

    @CacheEvict(value = "recipes", allEntries = true)
    public void deleteById(Long id) {
        postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recipe ID not found for " + id, "102"));
        postRepository.deleteById(id);
    }

    @CacheEvict(value = "recipes", allEntries = true)
    public RecipeResponse updateRecipe(RecipeRequest recipeRequest, Long id) {
        Recipe post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recipe not found", "102"));
        Category category = categoryRepository.findById(recipeRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found", "102"));
        post.setPrepTime(recipeRequest.getPrepTime());
        post.setDescription(recipeRequest.getDescription());
        post.setCookTime(recipeRequest.getCookTime());
        post.setAuthor(recipeRequest.getAuthor());
        post.setUserId(recipeRequest.getUserId());
        post.setCategory(category);
        Recipe postResponse = postRepository.save(post);
        return modelMapper.map(postResponse, RecipeResponse.class);
    }

    @Cacheable("recipes")
    public List<Recipe> getAllRecipes() {
        return postRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Cacheable("recipes")
    public List<RecipeRequest> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryId(categoryId).stream().map(p -> modelMapper.map(p, RecipeRequest.class)).collect(Collectors.toList());
    }
}
