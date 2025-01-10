package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import com.devsuperior.dscommerce.tests.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private List<Category> categories;

    @BeforeEach
    void setUp() throws Exception {
        category = CategoryFactory.createCategory();
        categories = new ArrayList<>();
        categories.add(category);

        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
    }

    @Test
    public void findAllShouldReturnListCategoryDTO() {
        List<CategoryDTO> categoriesDTO = categoryService.findAll();
        Assertions.assertEquals(categoriesDTO.size(), 1);
        Assertions.assertEquals(categoriesDTO.get(0).getId(), category.getId());
        Assertions.assertEquals(categoriesDTO.get(0).getName(), category.getName());
    }
}
