package fiap.scj.modulo1.application;

import static fiap.scj.modulo1.infrastructure.ProductServiceException.CREATE_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.DELETE_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.INVALID_PARAMETER_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.PRODUCT_NOT_FOUND_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.RETRIEVE_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.SEARCH_OPERATION_ERROR;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fiap.scj.modulo1.domain.Product;
import fiap.scj.modulo1.domain.ProductDetails;
import fiap.scj.modulo1.domain.repository.ProductDetailsRepository;
import fiap.scj.modulo1.infrastructure.ProductDetailsServiceException;
import fiap.scj.modulo1.infrastructure.ProductServiceException;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@Slf4j
public class ProductDetailsServiceImpl implements ProductDetailsService {

    private final ProductDetailsRepository repository;

    private final ObjectMapper objectMapper;

    @Autowired
    public ProductDetailsServiceImpl(ProductDetailsRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ProductDetails> search(Long id) throws ProductDetailsServiceException {
        try {
            List<ProductDetails> result = new ArrayList<>();

            log.info("Searching details from products for keyword={}", id);
            result.addAll(repository.findByProductId(id));
            return result;
            
        } catch (Exception e) {
            log.error("Error searching product", e);
            throw new ProductDetailsServiceException(SEARCH_OPERATION_ERROR, e);
        }
    }

    @Override
    public ProductDetails create(ProductDetails details) throws ProductDetailsServiceException {
        log.info("Creating product details ({})", details);
        try {
            if (details == null) {
                log.error("Invalid details");
                throw new ProductDetailsServiceException(INVALID_PARAMETER_ERROR, null);
            }
            ProductDetails result = repository.save(details);
            return result;
        } catch (Exception e) {
            log.error("Error creating product detail", e);
            throw new ProductDetailsServiceException(CREATE_OPERATION_ERROR, e);
        }
    }

    @Override
    public ProductDetails retrieve(Long id) throws ProductDetailsServiceException {
        log.info("Retrieving product for id={}", id);
        try {
            if (id == null) {
                log.error("Invalid id");
                throw new ProductServiceException(INVALID_PARAMETER_ERROR, null);
            }
            ProductDetails result = repository.findById(id).get();
            return result;
        } catch (Exception e) {
            log.error("Error creating product", e);
            throw new ProductDetailsServiceException(RETRIEVE_OPERATION_ERROR, e);
        }
    }

    @Override
    public ProductDetails update(Long id, ProductDetails details) throws ProductDetailsServiceException {
        log.info("Updating product ({}) for id={}", details, id);
        try {
            if (id == null || details == null) {
                log.error("Invalid id or product");
                throw new ProductDetailsServiceException(INVALID_PARAMETER_ERROR, null);
            }
            if (!repository.existsById(id)) {
                log.debug("Product not found for id={}", id);
                throw new ProductDetailsServiceException(PRODUCT_NOT_FOUND_ERROR, null);
            }
            ProductDetails result = repository.save(details);
            return result;
        } catch (Exception e) {
            log.error("Error creating product", e);
            throw new ProductDetailsServiceException(RETRIEVE_OPERATION_ERROR, e);
        }
    }

    @Override
    public void delete(Long id) throws ProductDetailsServiceException {
        log.info("Deleting product for id={}", id);
        try {
            if (id == null) {
                log.error("Invalid id or product");
                throw new ProductDetailsServiceException(INVALID_PARAMETER_ERROR, null);
            }
            if (!repository.existsById(id)) {
                log.debug("Product not found for id={}", id);
                throw new ProductDetailsServiceException(PRODUCT_NOT_FOUND_ERROR, null);
            }
            repository.deleteById(id);
        } catch (Exception e) {
            log.error("Error creating product", e);
            throw new ProductDetailsServiceException(DELETE_OPERATION_ERROR, e);
        }
    }
}
