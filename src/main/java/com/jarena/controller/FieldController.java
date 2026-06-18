package com.jarena.controller;

import com.jarena.model.Field;
import com.jarena.model.User;
import com.jarena.service.FieldService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class FieldController {

    @Autowired
    private FieldService fieldService;

    @GetMapping("/fields")
    public String customerFields(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User currentUser = AuthHelper.getLoggedInUser(session);
        List<Field> fields = fieldService.getAllFields();

        model.addAttribute("fields", fields);
        model.addAttribute("currentUser", currentUser);
        return "fields/customer-fields";
    }
}
