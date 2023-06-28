package com.smart.Controller;

import com.smart.DaoClasses.UserRepository;
import com.smart.entities.User;
import com.smart.helperClass.Message;
import com.smart.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    private Random random = new Random(1000);
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/forgot")
    public String openEmailForm(Model model)
    {
        model.addAttribute("title","Forgot password form");
        return "forgot_email_form";
    }

    @PostMapping("/send_otp")
    public String sendOTP(@RequestParam("email") String email, Model model, HttpSession session)
    {
        User user = userRepository.getUserByUserName(email);
        //we are checking if the entered email is already registered with us or not if not then we ask user to write a registered email
        if(user==null)
        {
            model.addAttribute("message",new com.smart.helperClass.Message("Email is not registered with us","alert-danger"));
            return "forgot_email_form";
        }

        //generating OTP
        int otp=random.nextInt(9999);

        //write code to send the email
        String subject="OTP from Smart Contact Manager";
        String message="OTP = "+otp;
        String to=email;

        boolean flag = emailService.sendEmail(subject,message,to);

        //if the above service which we are using is working correctly then method always send us true..even if email is not it will send true because service which we are using is working file
        //and if the service is not able to send the email then it will send an email to the recipient email from whose email otp is going to send with a message that email is not found
        if(flag)
        {
            session.setAttribute("myotp",otp);
            session.setAttribute("email",email);
            System.out.println("successfully send message");
            model.addAttribute("title","OTP verification");
            model.addAttribute("message",new com.smart.helperClass.Message("Enter the OTP send to your email address","alert-success"));

            return "verify_otp";
        }

        //this below code only works if there is any issue with the service itself that we are using then that method will send false with an exception
        model.addAttribute("message",new Message("Try again later !!","alert-danger"));
        model.addAttribute("title","Forgot password form");
      return "forgot_email_form";
    }

    //verify the OTP
    @PostMapping("/verify_otp")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session, Model model)
    {
        int myOTP = (int)session.getAttribute("myotp");

        if(myOTP==otp)
        {
            //password change form
            model.addAttribute("title","Change Password");
            return "password_change_form";
        }

        model.addAttribute("message",new com.smart.helperClass.Message("You have entered wrong OTP","alert-danger"));
        model.addAttribute("title","OTP verification");
        return "verify_otp";
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam("newPassword") String newPassword,HttpSession session, Model model)
    {
            String userEmail = (String )session.getAttribute("email");
            User user=userRepository.getUserByUserName(userEmail);

            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);

            return "redirect:/signin?change=password changed successfully..";
    }




}
