package com.smart.Controller;


import com.smart.DaoClasses.ContactRepository;
import com.smart.DaoClasses.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String name, Principal principal) //instead of "?" you can also use "List<Contact>"
    {
        User user=userRepository.getUserByUserName(principal.getName());
        List<Contact> contacts=contactRepository.findByNameContainingAndUser(name,user);

        return ResponseEntity.ok(contacts);
    }
}
