package fiap.scj.modulo1.interfaces;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fiap.scj.modulo1.application.ProductDetailsService;
import fiap.scj.modulo1.domain.ProductDetails;
import fiap.scj.modulo1.infrastructure.ProductDetailsServiceException;
import lombok.extern.slf4j.Slf4j;

import static fiap.scj.modulo1.infrastructure.ProductServiceException.CREATE_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.DELETE_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.PRODUCT_NOT_FOUND_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.RETRIEVE_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.SEARCH_OPERATION_ERROR;
import static fiap.scj.modulo1.infrastructure.ProductServiceException.UPDATE_OPERATION_ERROR;

@RestController
@RequestMapping("/products/{productId}/details")
@Slf4j
public class ProductDetailsResource {

    private final ProductDetailsService service;

    @Autowired
    public ProductDetailsResource(ProductDetailsService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(code = HttpStatus.OK)
    public List<ProductDetails> search(@PathVariable("productId") Long id) {
        log.info("Processing search request");
        try {
            List<ProductDetails> result = service.search(id);
            return result;
        } catch (ProductDetailsServiceException e) {
            log.error("Error processing search request", e);
            throw exceptionHandler(e);
        }
    }

    private ResponseStatusException exceptionHandler(ProductDetailsServiceException e) {
        if (e.getOperation() == null || e.getOperation().isEmpty()) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (SEARCH_OPERATION_ERROR.equals(e.getOperation())
                || CREATE_OPERATION_ERROR.equals(e.getOperation())
                || RETRIEVE_OPERATION_ERROR.equals(e.getOperation())
                || UPDATE_OPERATION_ERROR.equals(e.getOperation())
                || DELETE_OPERATION_ERROR.equals(e.getOperation())) {
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (PRODUCT_NOT_FOUND_ERROR.equals(e.getOperation())) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody ProductDetails details) throws ProductDetailsServiceException {
        log.info("Processing create request");
        try {
        	ProductDetails result = service.create(details);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                    "/{id}").buildAndExpand(result.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (ProductDetailsServiceException e) {
            log.error("Error processing create request", e);
            throw exceptionHandler(e);
        }
    }


    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ProductDetails retrieve(@PathVariable Long id) throws ProductDetailsServiceException {
        log.info("Processing retrieve request");
        try {
            return service.retrieve(id);
        } catch (ProductDetailsServiceException e) {
            log.error("Error processing retrieve request", e);
            throw exceptionHandler(e);
        }
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ProductDetails update(@PathVariable Long id, @RequestBody ProductDetails product) throws ProductDetailsServiceException {
        log.info("Processing update request");
        try {
            return service.update(id, product);
        } catch (ProductDetailsServiceException e) {
            log.error("Error processing update request", e);
            throw exceptionHandler(e);
        }
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(@PathVariable Long id) throws ProductDetailsServiceException {
        log.info("Processing delete request");
        try {
            service.delete(id);
        } catch (ProductDetailsServiceException e) {
            log.error("Error processing delete request", e);
            throw exceptionHandler(e);
        }
    }
}
