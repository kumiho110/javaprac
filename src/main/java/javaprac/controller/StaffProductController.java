package javaprac.controller;

import javaprac.service.StaffProductService;
import javaprac.service.dto.StaffProductFormCommand;
import javaprac.service.dto.StaffProductFormData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javaprac.service.ProductCatalogService;

@Controller
@RequestMapping("/staff/products")
public class StaffProductController {

    private final StaffProductService staffProductService;
    private final ProductCatalogService productCatalogService;

    public StaffProductController(StaffProductService staffProductService, ProductCatalogService productCatalogService) {
        this.staffProductService = staffProductService;
        this.productCatalogService = productCatalogService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String attributeName,
            @RequestParam(required = false) String attributeValue,
            Model model
    ) {
        model.addAttribute("products", productCatalogService.search(type, manufacturer, attributeName, attributeValue));
        model.addAttribute("types", productCatalogService.listTypes());
        model.addAttribute("manufacturers", productCatalogService.listManufacturers());

        model.addAttribute("qType", type == null ? "" : type);
        model.addAttribute("qManufacturer", manufacturer == null ? "" : manufacturer);
        model.addAttribute("qAttributeName", attributeName == null ? "" : attributeName);
        model.addAttribute("qAttributeValue", attributeValue == null ? "" : attributeValue);

        model.addAttribute("pageTitle", "Управление товарами");
        return "products_staff";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long typeId, Model model) {
        if (typeId == null) {
            applyFormData(model, staffProductService.buildCreateFormData());
        } else {
            applyFormData(model, staffProductService.buildCreateFormData(typeId));
        }
        return "product_form_staff";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        applyFormData(model, staffProductService.buildEditFormData(id));
        return "product_form_staff";
    }

    @PostMapping
    public String create(
            @RequestParam(required = false) Long typeId,
            @RequestParam Long manufacturerId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam java.math.BigDecimal price,
            @RequestParam Integer stockQty,
            @RequestParam(name = "attributeId", required = false) java.util.List<Long> attributeIds,
            @RequestParam(name = "attributeValue", required = false) java.util.List<String> attributeValues,
            Model model
    ) {
        StaffProductFormCommand cmd = new StaffProductFormCommand(
                typeId,
                manufacturerId,
                name,
                description,
                price,
                stockQty,
                attributeIds,
                attributeValues
        );

        try {
            staffProductService.createProduct(cmd);
            return "redirect:/staff/products";
        } catch (Exception ex) {
            applyFormData(model, staffProductService.buildCreateFormDataFromCommand(cmd));
            model.addAttribute("errorMessage", "Не удалось создать товар: " + ex.getMessage());
            return "product_form_staff";
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam(required = false) Long typeId,
            @RequestParam Long manufacturerId,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam java.math.BigDecimal price,
            @RequestParam Integer stockQty,
            @RequestParam(name = "attributeId", required = false) java.util.List<Long> attributeIds,
            @RequestParam(name = "attributeValue", required = false) java.util.List<String> attributeValues,
            Model model
    ) {
        StaffProductFormCommand cmd = new StaffProductFormCommand(
                typeId,
                manufacturerId,
                name,
                description,
                price,
                stockQty,
                attributeIds,
                attributeValues
        );

        try {
            staffProductService.updateProduct(id, cmd);
            return "redirect:/staff/products";
        } catch (Exception ex) {
            applyFormData(model, staffProductService.buildEditFormDataFromCommand(id, cmd));
            model.addAttribute("errorMessage", "Не удалось сохранить товар: " + ex.getMessage());
            return "product_form_staff";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            staffProductService.deleteProduct(id);
            ra.addFlashAttribute("successMessage", "Товар удалён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", "Ошибка удаления товара: " + ex.getMessage());
        }

        return "redirect:/staff/products";
    }

    private void applyFormData(Model model, StaffProductFormData data) {
        model.addAttribute("product", data.product());
        model.addAttribute("types", data.types());
        model.addAttribute("manufacturers", data.manufacturers());
        model.addAttribute("attributes", data.attributes());
        model.addAttribute("formMode", data.formMode());
        model.addAttribute("qTypeId", data.qTypeId());
        model.addAttribute("qManufacturerId", data.qManufacturerId());
    }

    @GetMapping("/type-attributes")
    @ResponseBody
    public java.util.List<javaprac.service.dto.StaffProductAttributeValueInput> typeAttributes(
            @RequestParam Long typeId
    ) {
        return staffProductService.getAttributeInputsForType(typeId);
    }
}