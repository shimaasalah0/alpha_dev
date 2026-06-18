package com.jarena.controller;

import com.jarena.model.Field;
import com.jarena.model.User;
import com.jarena.service.FieldService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/fields")
public class AdminFieldController {

    @Autowired
    private FieldService fieldService;

    @GetMapping
    public String adminFields(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        User currentUser = AuthHelper.getLoggedInUser(session);
        List<Field> fields = fieldService.getAllFields();
        long totalFields = fieldService.getTotalFields();
        long availableCount = fieldService.getAvailableFieldsCount();

        model.addAttribute("fields", fields);
        model.addAttribute("totalFields", totalFields);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("currentUser", currentUser);
        return "fields/admin-fields";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        model.addAttribute("field", new Field());
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
        return "fields/create-field";
    }

    @PostMapping("/create")
    public String createField(HttpSession session,
                              @ModelAttribute("field") Field field,
                              Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        String error = fieldService.addField(field);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
            return "fields/create-field";
        }
        return "redirect:/admin/fields";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(HttpSession session,
                               @PathVariable("id") long id,
                               Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Field field = fieldService.getFieldById(id);
        if (field == null) return "redirect:/admin/fields";

        model.addAttribute("field", field);
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
        return "fields/edit-field";
    }

    @PostMapping("/edit/{id}")
    public String editField(HttpSession session,
                            @PathVariable("id") long id,
                            @ModelAttribute("field") Field formField,
                            Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Field existing = fieldService.getFieldById(id);
        if (existing == null) return "redirect:/admin/fields";

        existing.setName(formField.getName());
        existing.setLocation(formField.getLocation());
        existing.setPricePerHour(formField.getPricePerHour());
        existing.setAvailable(formField.isAvailable());
        existing.setImageUrl(formField.getImageUrl());

        String error = fieldService.updateField(existing);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("field", existing);
            model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
            return "fields/edit-field";
        }
        return "redirect:/admin/fields";
    }

    @PostMapping("/delete/{id}")
    public String deleteField(HttpSession session,
                              @PathVariable("id") long id) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        String error = fieldService.deleteField(id);
        if (error != null) {
            return "redirect:/admin/fields?error=" + error.replace(" ", "+");
        }
        return "redirect:/admin/fields";
    }

    @PostMapping("/toggle/{id}")
    public String toggleAvailability(HttpSession session,
                                     @PathVariable("id") long id) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Field field = fieldService.getFieldById(id);
        if (field != null) {
            field.setAvailable(!field.isAvailable());
            fieldService.updateField(field);
        }
        return "redirect:/admin/fields";
    }
}
