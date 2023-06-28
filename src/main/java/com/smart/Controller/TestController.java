package com.smart.Controller;

import com.smart.DaoClasses.UserRepository;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class TestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @RequestMapping("/")
    public String home(Model model)
    {
        model.addAttribute("title","home-smart contact manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model)
    {
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model)
    {

        model.addAttribute("title","Register-smart contact manager");
        model.addAttribute("user",new User());  //we are using this becasue at first when he click on signup the page with the form will display and the fields will be filled with blank data....if we remove this then it will impact the page becassue there we are already using $user.name and $user.about and etc therefore if we remove this then that fields will give us error
        //yes you can say that okay in that case remove it from the signup page also but we cant becasue once the form is filled then we want to show its own information to the user therefore we need are using it

        return "signup";
    }

    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(value="agreement", defaultValue ="false") Boolean agreement, Model model)
    {
        if(result.hasErrors())
        {
            System.out.println(result);
            return "signup";
        }

        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User user1=userRepository.save(user);
        System.out.println(user1);

        //to give message that form successfully submitted
        model.addAttribute("message",new com.smart.helperClass.Message("Successfully Registered","alert-success"));
        return "signup";
    }

    @RequestMapping("/signin")
    public String customLogin()
    {
        return "login";
    }

}
