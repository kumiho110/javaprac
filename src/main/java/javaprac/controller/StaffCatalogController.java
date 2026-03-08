package javaprac.controller;

import javaprac.service.StaffCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/catalog")
public class StaffCatalogController {

    private final StaffCatalogService staffCatalogService;

    public StaffCatalogController(StaffCatalogService staffCatalogService) {
        this.staffCatalogService = staffCatalogService;
    }

    @GetMapping
    public String home() {
        return "catalog_staff";
    }


    @GetMapping("/types")
    public String types(Model model) {
        model.addAttribute("types", staffCatalogService.listTypes());
        model.addAttribute("pageTitle", "Типы товаров");
        return "product_types_staff";
    }

    @GetMapping("/types/new")
    public String typeCreateForm(Model model) {
        model.addAttribute("formMode", "create");
        return "product_type_form_staff";
    }

    @GetMapping("/types/{id}/edit")
    public String typeEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("type", staffCatalogService.getType(id));
            model.addAttribute("formMode", "edit");
            return "product_type_form_staff";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/types";
        }
    }

    @PostMapping("/types")
    public String typeCreate(@RequestParam String name, RedirectAttributes ra) {
        try {
            staffCatalogService.createType(name);
            ra.addFlashAttribute("successMessage", "Тип товара создан");
            return "redirect:/staff/catalog/types";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/types/new";
        }
    }

    @PostMapping("/types/{id}")
    public String typeUpdate(@PathVariable Long id, @RequestParam String name, RedirectAttributes ra) {
        try {
            staffCatalogService.updateType(id, name);
            ra.addFlashAttribute("successMessage", "Тип товара сохранён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/catalog/types";
    }

    @PostMapping("/types/{id}/delete")
    public String typeDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            staffCatalogService.deleteType(id);
            ra.addFlashAttribute("successMessage", "Тип товара удалён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/catalog/types";
    }


    @GetMapping("/manufacturers")
    public String manufacturers(Model model) {
        model.addAttribute("manufacturers", staffCatalogService.listManufacturers());
        model.addAttribute("pageTitle", "Производители");
        return "manufacturers_staff";
    }

    @GetMapping("/manufacturers/new")
    public String manufacturerCreateForm(Model model) {
        model.addAttribute("formMode", "create");
        return "manufacturer_form_staff";
    }

    @GetMapping("/manufacturers/{id}/edit")
    public String manufacturerEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("manufacturer", staffCatalogService.getManufacturer(id));
            model.addAttribute("formMode", "edit");
            return "manufacturer_form_staff";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/manufacturers";
        }
    }

    @PostMapping("/manufacturers")
    public String manufacturerCreate(
            @RequestParam String name,
            @RequestParam String assemblyCountry,
            RedirectAttributes ra
    ) {
        try {
            staffCatalogService.createManufacturer(name, assemblyCountry);
            ra.addFlashAttribute("successMessage", "Производитель создан");
            return "redirect:/staff/catalog/manufacturers";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/manufacturers/new";
        }
    }

    @PostMapping("/manufacturers/{id}")
    public String manufacturerUpdate(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String assemblyCountry,
            RedirectAttributes ra
    ) {
        try {
            staffCatalogService.updateManufacturer(id, name, assemblyCountry);
            ra.addFlashAttribute("successMessage", "Производитель сохранён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/catalog/manufacturers";
    }

    @PostMapping("/manufacturers/{id}/delete")
    public String manufacturerDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            staffCatalogService.deleteManufacturer(id);
            ra.addFlashAttribute("successMessage", "Производитель удалён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/staff/catalog/manufacturers";
    }



    @GetMapping("/types/{typeId}/attributes")
    public String typeAttributes(@PathVariable Long typeId, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("type", staffCatalogService.getType(typeId));
            model.addAttribute("attributes", staffCatalogService.listTypeAttributes(typeId));
            return "type_attributes_staff";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/types";
        }
    }

    @GetMapping("/types/{typeId}/attributes/new")
    public String attributeCreateForm(@PathVariable Long typeId, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("type", staffCatalogService.getType(typeId));
            model.addAttribute("formMode", "create");
            return "type_attribute_form_staff";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/types";
        }
    }

    @GetMapping("/attributes/{id}/edit")
    public String attributeEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            var attr = staffCatalogService.getTypeAttribute(id);
            model.addAttribute("attribute", attr);
            model.addAttribute("type", attr.getProductType());
            model.addAttribute("formMode", "edit");
            return "type_attribute_form_staff";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/types";
        }
    }

    @PostMapping("/types/{typeId}/attributes")
    public String attributeCreate(
            @PathVariable Long typeId,
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean required,
            RedirectAttributes ra
    ) {
        try {
            staffCatalogService.createTypeAttribute(typeId, name, required);
            ra.addFlashAttribute("successMessage", "Характеристика создана");
            return "redirect:/staff/catalog/types/" + typeId + "/attributes";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/staff/catalog/types/" + typeId + "/attributes/new";
        }
    }

    @PostMapping("/attributes/{id}")
    public String attributeUpdate(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean required,
            RedirectAttributes ra
    ) {
        Long typeId = null;

        try {
            var attr = staffCatalogService.getTypeAttribute(id);
            typeId = attr.getProductType().getId();

            staffCatalogService.updateTypeAttribute(id, name, required);
            ra.addFlashAttribute("successMessage", "Характеристика сохранена");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return typeId == null
                ? "redirect:/staff/catalog/types"
                : "redirect:/staff/catalog/types/" + typeId + "/attributes";
    }

    @PostMapping("/attributes/{id}/move-up")
    public String attributeMoveUp(@PathVariable Long id, RedirectAttributes ra) {
        Long typeId = null;

        try {
            var attr = staffCatalogService.getTypeAttribute(id);
            typeId = attr.getProductType().getId();

            staffCatalogService.moveTypeAttributeUp(id);
            ra.addFlashAttribute("successMessage", "Характеристика перемещена вверх");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return typeId == null
                ? "redirect:/staff/catalog/types"
                : "redirect:/staff/catalog/types/" + typeId + "/attributes";
    }

    @PostMapping("/attributes/{id}/move-down")
    public String attributeMoveDown(@PathVariable Long id, RedirectAttributes ra) {
        Long typeId = null;

        try {
            var attr = staffCatalogService.getTypeAttribute(id);
            typeId = attr.getProductType().getId();

            staffCatalogService.moveTypeAttributeDown(id);
            ra.addFlashAttribute("successMessage", "Характеристика перемещена вниз");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return typeId == null
                ? "redirect:/staff/catalog/types"
                : "redirect:/staff/catalog/types/" + typeId + "/attributes";
    }

    @PostMapping("/attributes/{id}/delete")
    public String attributeDelete(@PathVariable Long id, RedirectAttributes ra) {
        Long typeId = null;

        try {
            var attr = staffCatalogService.getTypeAttribute(id);
            typeId = attr.getProductType().getId();

            staffCatalogService.deleteTypeAttribute(id);
            ra.addFlashAttribute("successMessage", "Характеристика удалена");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return typeId == null
                ? "redirect:/staff/catalog/types"
                : "redirect:/staff/catalog/types/" + typeId + "/attributes";
    }
}
