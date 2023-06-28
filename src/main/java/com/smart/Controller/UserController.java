package com.smart.Controller;

import com.smart.DaoClasses.ContactRepository;
import com.smart.DaoClasses.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helperClass.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @ModelAttribute
    public void addCommonData(Model model,Principal principal)
    {
        String userName=principal.getName();
        //get the user using userName
        User user=userRepository.getUserByUserName(userName);
        //System.out.println(user);
        model.addAttribute("user",user);
    }

    //user dashboard
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal)
    {
        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }



    //Add Contact handler
    @RequestMapping("/add_contact")
    public String openAddContactForm(Model model)
    {
        model.addAttribute("title","Add Contact");
        return "normal/add_contact_form";
    }




    //After submitting the Add Contact form  this handler will process and work
    @PostMapping("/process_contact")
    public String processAddContactForm(@ModelAttribute("contact") Contact contact, Principal principal, @RequestParam("profileImage")MultipartFile multipartFile,Model model)
    {

        try {
            model.addAttribute("title","Add Contact");
            String userName = principal.getName();
            User user = userRepository.getUserByUserName(userName);

           // System.out.println(contact);

            if(multipartFile.isEmpty())
            {
                System.out.println("file is empty");
                contact.setImageUrl("contact.png");
            }
            else
            {
                contact.setImageUrl(multipartFile.getOriginalFilename());

                File saveFile=new ClassPathResource("/static/image").getFile();
                Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("image is uploaded successfully");
            }
            contact.setUser(user);
            user.getContacts().add(contact);
            userRepository.save(user);
            //to give message that form successfully submitted
            model.addAttribute("message",new Message("Successfully Added","alert-success"));


        }catch (Exception e)
        {
            System.out.println(e.getMessage());
            model.addAttribute("message",new Message("Something went wrong!!try again","alert-danger"));

        }

        return "normal/add_contact_form";
    }


    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page, Model model,Principal principal)
    {

        model.addAttribute("title","View Contacts");

        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);

        Pageable pageable= PageRequest.of(page,5);
        Page<Contact> contacts=contactRepository.getContactByUser(user.getId(),pageable);

        model.addAttribute("contacts",contacts);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",contacts.getTotalPages());

        return "normal/show_contacts_page";
    }

    //showing particular contact details
    @GetMapping("/contact/{cid}")
    public String showContactDetail(@PathVariable("cid")  Integer cid,Model model,Principal principal)
    {
        Optional<Contact> contactOptional = contactRepository.findById(cid);
        Contact contact= contactOptional.get();

        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);

        if(user.getId()==contact.getUser().getId())
        {
            model.addAttribute("contact", contact);
            model.addAttribute("title",contact.getName());
        }
        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid,Model model,Principal principal)
    {
       // String userName= principal.getName();
        User user =userRepository.getUserByUserName(principal.getName());

       // Optional<Contact> contactOptional = contactRepository.findById(cid);
        Contact contact = contactRepository.findById(cid).get();
        try {
            if (user.getId() == contact.getUser().getId()) {
                contact.setUser(null); //we have unlinked this contact from the user so that it can be easily deleted

                if(!contact.getImageUrl().equals("contact.png"))
                {
                    File deleteFile = new ClassPathResource("/static/image").getFile();
                    File file1 = new File(deleteFile,contact.getImageUrl());
                    file1.delete();
                }

                contactRepository.delete(contact);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return "redirect:/user/show_contacts/0";
    }

    //it will open the update form page
    @PostMapping("/update_contact/{cid}")
    public String updateForm(@PathVariable("cid")Integer cid, Model model)
    {
        model.addAttribute("title","Update Contact");

        Contact contact= contactRepository.findById(cid).get();
        model.addAttribute("contact",contact);

        return "normal/update_form";
    }


    //processing updated contact form here
    @PostMapping("/process_updated_form")
    public String processUpdatedContactForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile multipartFile,Principal principal)
    {
        try{
            //fetching old contact detail so that we can get the image name
            Optional<Contact> con=contactRepository.findById(contact.getId());
            Contact oldContact=con.get();

            //if user updated a new image then this will work
            if(!multipartFile.isEmpty())
            {
                //delete old pic
                File deleteFile=new ClassPathResource("/static/image").getFile();
                File file1=new File(deleteFile,oldContact.getImageUrl());
                file1.delete();

                //update new image
                File saveFile=new ClassPathResource("/static/image").getFile();
                Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImageUrl(multipartFile.getOriginalFilename());

            }else
            {
                //otherwise we will update the existing image because modelattribute will set the image as null
                contact.setImageUrl(oldContact.getImageUrl());
            }


            User user=userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            contactRepository.save(contact);

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return "redirect:/user/contact/"+contact.getId();
    }

    //Your Profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model)
    {
        model.addAttribute("title","Profile page");

        return "normal/profile_page";
    }

    //change password form (setting side menu bar handler
    @GetMapping("/settings")
    public String changePasswordForm(Model model)
    {
        model.addAttribute("title","settings");
        return "normal/settings";
    }

    //change password form handler
    @PostMapping("/change_password")
    public String processChangePasswordForm(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,Principal principal,Model model)
    {
        model.addAttribute("title","settings");
        User user=userRepository.getUserByUserName(principal.getName());

        if(bCryptPasswordEncoder.matches(oldPassword,user.getPassword()))
        {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            model.addAttribute("message",new Message("Password changed successfully !!","alert-success"));
        }
        else {

            model.addAttribute("message",new Message("Old password is incorrect !!try again","alert-danger"));
        }

       return"normal/settings";
    }

}
