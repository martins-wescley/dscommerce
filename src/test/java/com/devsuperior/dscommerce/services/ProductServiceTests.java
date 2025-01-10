package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Long existingProductId, nomExistingProductId, dependentProductId;
    private String productName;
    private Product product;
    private ProductDTO productDTO;
    private PageImpl<Product> page;

    @BeforeEach
    public void setUp() throws Exception {
        existingProductId = 1L;
        nomExistingProductId = 2L;
        dependentProductId = 3L;

        productName = "Playstation 5";

        product = ProductFactory.createProduct(productName);
        productDTO = new ProductDTO(product);
        page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nomExistingProductId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.searchByName(any(), (Pageable) any())).thenReturn(page);

        Mockito.when(productRepository.save(any())).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nomExistingProductId)).thenThrow(EntityNotFoundException.class);


        //Delete
        Mockito.when(productRepository.existsById(existingProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(dependentProductId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nomExistingProductId)).thenReturn(false);

        Mockito.doNothing().when(productRepository).deleteById(existingProductId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentProductId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO productDTO = productService.findById(existingProductId);

        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(productDTO.getId(), existingProductId);
        Assertions.assertEquals(productDTO.getName(), product.getName());
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nomExistingProductId);
        });
    }

    @Test
    public void findAllShouldReturnPagedProductMinDTO () {
        Pageable pageable = PageRequest.of(0,12);

        Page<ProductMinDTO> result = productService.findAll(productName, pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(), 1);
        Assertions.assertEquals(result.iterator().next().getName(), productName);
    }

    @Test
    public void insertShouldReturnProductDTO () {
        ProductDTO result = productService.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), product.getId());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists () {
        ProductDTO result = productService.update(existingProductId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), product.getName());
    }

    @Test
    public void updatedShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nomExistingProductId, productDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists () {
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingProductId);
        });
    }

    @Test
    public void deleteShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nomExistingProductId);
        });
    }

    @Test
    public void deleteShouldReturnDataIntegrityViolationExceptionWhenExistDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentProductId);
        });
    }
}
