package com.ridvansevik.library_app.controller;

import com.ridvansevik.library_app.model.Loan;
import com.ridvansevik.library_app.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/profile")
public class ProfileViewController {

    private final LoanService loanService;

    @GetMapping
    public String showProfile(Model model, Principal principal){
        if(principal ==null){
            return "redirect:/web/login";
        }
        String username = principal.getName();
        List<Loan> userLoans = loanService.findLoansByUsername(username);
        model.addAttribute("username",username);
        model.addAttribute("loans",userLoans);
        return "profile";
    }

}
