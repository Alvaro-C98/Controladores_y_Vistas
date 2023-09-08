package com.mvc.authentication.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mvc.authentication.models.User;
import com.mvc.authentication.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {
	private final UserService userService;
    public MainController(UserService user) {
        this.userService = user;
    }
    
    @GetMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    
    @GetMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
    
    @PostMapping("/registration")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	if(result.hasErrors()) {
    		return "registrationPage.jsp";
    	}else {
    		User u = userService.registerUser(user);
    		session.setAttribute("userId", u.getId());
    		return "redirect:/home";
    	}
    }
    
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
    	boolean isAuthentication = userService.authenticateUser(email, password);
    	if(isAuthentication) {
    		User u = userService.findByEmail(email);
    		session.setAttribute("userId", u.getId());
    		return "redirect:/home";
    	}else {
    		model.addAttribute("error", "invalid credentials. Please try again.");
    		return "loginPage.jsp";
    	}
    }
    
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
    	Long userId = (Long) session.getAttribute("userId");
    	User u = userService.findUserById(userId);
    	model.addAttribute("user",u);
    	return "homePage.jsp";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/login";
    }
}
