package javaprac.controller;

import jakarta.servlet.http.HttpServletRequest;
import javaprac.service.NavViewService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavControllerAdvice {

    private final NavViewService navViewService;

    public NavControllerAdvice(NavViewService navViewService) {
        this.navViewService = navViewService;
    }

    @ModelAttribute
    public void fillNav(HttpServletRequest request) {
        navViewService.fillNav(request);
    }
}