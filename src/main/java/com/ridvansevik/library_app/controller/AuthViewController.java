package com.ridvansevik.library_app.controller;


import com.ridvansevik.library_app.dto.RegisterDto;
import com.ridvansevik.library_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A traditional Spring MVC Controller to handle requests for the web interface's
 * authentication pages (login and registration).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/web") // All endpoints are prefixed with /web
public class AuthViewController {

    private final AuthService authService;

    /**
     * Displays the login page.
     * @return The name of the login view template.
     */
    @GetMapping("/login")
    public String showLoginForm(){
        return "login"; // Renders the "login.html" template
    }

    /**
     * Displays the user registration page.
     * @param model The model to pass data to the view.
     * @return The name of the register view template.
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model){
        // Add an empty DTO to the model to bind form data to.
        model.addAttribute("user", new RegisterDto());
        return "register"; // Renders the "register.html" template
    }

    /**
     * Processes the registration form submission.
     * @param registerDto The object populated with form data.
     * @param bindingResult Captures validation errors.
     * @param model The model to pass data back to the view in case of errors.
     * @return A redirect string on success, or the view name on error.
     */
    @PostMapping("/register/save")
    public String registerUser(@Valid @ModelAttribute("user") RegisterDto registerDto, BindingResult bindingResult, Model model){

        // If validation errors exist (e.g., empty fields), return to the form.
        if(bindingResult.hasErrors()){
            return "register";
        }

        try{
            // Attempt to register the user via the service layer.
            authService.register(registerDto);
        } catch (IllegalStateException e){
            // If the service throws an error (e.g., username exists), show it on the form.
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }

        // On success, redirect to prevent duplicate submission on refresh (Post-Redirect-Get pattern).
        return "redirect:/web/register?success";
    }
}