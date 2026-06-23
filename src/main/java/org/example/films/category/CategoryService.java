package org.example.films.category;

import java.util.List;

import org.example.films.common.NotFoundException;
import org.example.films.content.Content;
import org.example.films.content.ContentRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;

    public CategoryService(CategoryRepository categoryRepository, ContentRepository contentRepository) {
        this.categoryRepository = categoryRepository;
        this.contentRepository = contentRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category get(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kategorie", id));
    }

    public Category create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name().trim());
        return categoryRepository.save(category);
    }

    public Category update(String id, CategoryRequest request) {
        Category category = get(id);
        category.setName(request.name().trim());
        return categoryRepository.save(category);
    }

    public void delete(String id) {
        Category category = get(id);
        List<Content> contents = contentRepository.findByCategoryIdsContaining(id);
        contents.forEach(content -> content.setCategoryIds(
                content.getCategoryIds().stream()
                        .filter(categoryId -> !categoryId.equals(id))
                        .toList()
        ));
        contentRepository.saveAll(contents);
        categoryRepository.delete(category);
    }
}
