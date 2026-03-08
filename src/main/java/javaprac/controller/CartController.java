package javaprac.controller;

import javaprac.model.OrderStatus;
import javaprac.service.CartService;
import javaprac.service.CurrentUserService;
import javaprac.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public CartController(CartService cartService,
                          OrderService orderService,
                          CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/cart")
    public String cartPage(Model model) {
        var user = currentUserService.currentUser();
        var cart = cartService.getOrCreateCart(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int qty,
            @RequestHeader(value = "Referer", required = false) String referer,
            RedirectAttributes ra
    ) {
        try {
            var user = currentUserService.currentUser();
            cartService.addToCart(user.getId(), productId, qty);
            ra.addFlashAttribute("success", "Товар добавлен в корзину");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:" + (referer == null || referer.isBlank() ? "/products" : referer);
    }

    @PostMapping("/cart/item/{itemId}/qty")
    public String updateQty(
            @PathVariable Long itemId,
            @RequestParam int qty,
            RedirectAttributes ra
    ) {
        try {
            var user = currentUserService.currentUser();
            cartService.updateCartItemQty(user.getId(), itemId, qty);
            ra.addFlashAttribute("success", "Количество обновлено");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/item/{itemId}/remove")
    public String removeItem(
            @PathVariable Long itemId,
            RedirectAttributes ra
    ) {
        try {
            var user = currentUserService.currentUser();
            cartService.removeFromCart(user.getId(), itemId);
            ra.addFlashAttribute("success", "Товар удалён из корзины");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(
            @RequestParam String deliveryAddress,
            @RequestParam(required = false) String deliveryTimeWindow,
            RedirectAttributes ra
    ) {
        try {
            var user = currentUserService.currentUser();
            var order = cartService.checkout(user.getId(), deliveryAddress, deliveryTimeWindow);
            ra.addFlashAttribute("success", "Заказ оформлен");
            return "redirect:/my/orders/" + order.getId();
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/my/orders")
    public String myOrders(Model model) {
        var user = currentUserService.currentUser();
        model.addAttribute("orders", cartService.getPlacedOrders(user.getId()));
        return "my_orders";
    }

    @GetMapping("/my/orders/{id}")
    public String myOrderDetails(@PathVariable Long id, Model model) {
        var user = currentUserService.currentUser();

        var order = orderService.getOrderForUserView(user.getId(), id);

        if (order.getStatus() == OrderStatus.cart) {
            throw new IllegalArgumentException("Корзина не является оформленным заказом");
        }

        model.addAttribute("order", order);
        return "order_status";
    }

    @PostMapping("/my/orders/{id}/cancel")
    public String cancelMyOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var user = currentUserService.currentUser();
            orderService.cancelOrderByUser(user.getId(), id);
            ra.addFlashAttribute("success", "Заказ отменён");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/my/orders/" + id;
    }
}