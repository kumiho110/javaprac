package javaprac.controller;

import jakarta.servlet.http.HttpServletRequest;
import javaprac.service.CatalogViewService;
import javaprac.service.ProductCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductCatalogService productCatalogService;
    private final CatalogViewService catalogViewService;

    public ProductController(ProductCatalogService productCatalogService,
                             CatalogViewService catalogViewService) {
        this.productCatalogService = productCatalogService;
        this.catalogViewService = catalogViewService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String attributeName,
            @RequestParam(required = false) String attributeValue,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("products", productCatalogService.search(type, manufacturer, attributeName, attributeValue));
        model.addAttribute("types", productCatalogService.listTypes());
        model.addAttribute("manufacturers", productCatalogService.listManufacturers());

        model.addAttribute("qType", type == null ? "" : type);
        model.addAttribute("qManufacturer", manufacturer == null ? "" : manufacturer);
        model.addAttribute("qAttributeName", attributeName == null ? "" : attributeName);
        model.addAttribute("qAttributeValue", attributeValue == null ? "" : attributeValue);

        catalogViewService.fillCartInfo(model);
        catalogViewService.fillContinueUrl(model, request);

        return "products";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, HttpServletRequest request, Model model) {
        var product = productCatalogService.getProductDetails(id);
        model.addAttribute("product", product);
        model.addAttribute("attributeRows", productCatalogService.buildAttributeRows(product));
        catalogViewService.fillCartInfo(model);
        catalogViewService.fillContinueUrl(model, request);
        return "product";
    }

    @GetMapping("/attribute-names")
    @ResponseBody
    public List<String> attributeNames(@RequestParam String type) {
        if (type == null || type.isBlank()) {
            return List.of();
        }
        return productCatalogService.listAttributeNamesByType(type.trim());
    }

    @GetMapping("/attribute-values")
    @ResponseBody
    public List<String> attributeValues(@RequestParam String type, @RequestParam String attributeName) {
        if (type == null || type.isBlank() || attributeName == null || attributeName.isBlank()) {
            return List.of();
        }
        return productCatalogService.listAttributeValuesByTypeAndName(type.trim(), attributeName.trim());
    }
}