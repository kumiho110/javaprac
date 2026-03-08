package javaprac.controller;

import javaprac.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/orders")
public class StaffOrderController {

    private final OrderService orderService;

    public StaffOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        try {
            model.addAttribute("orders", orderService.listStaffOrders(status));
        } catch (Exception ex) {
            model.addAttribute("orders", orderService.listStaffOrders(null));
            model.addAttribute("errorMessage", ex.getMessage());
        }

        model.addAttribute("qStatus", status == null ? "" : status);
        return "orders_staff";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getStaffOrderDetails(id));
        return "order_details_staff";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam javaprac.model.OrderStatus status,
            RedirectAttributes ra
    ) {
        try {
            orderService.updateOrderStatusByStaff(id, status);
            ra.addFlashAttribute("successMessage", "Статус заказа обновлён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/staff/orders/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            orderService.deleteCancelledDeliveredOrder(id);
            ra.addFlashAttribute("successMessage", "Заказ удалён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/staff/orders";
    }
}