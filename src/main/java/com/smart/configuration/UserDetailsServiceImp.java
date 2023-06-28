package com.smart.configuration;

import com.smart.DaoClasses.UserRepository;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException //While calling this by spring security it will get email from form user enter in login time
    {
        User user=userRepository.getUserByUserName(username);
        if(user==null)
        {
            throw  new UsernameNotFoundException("Could not found user!!");
        }

        CustomerUserDetails customerUserDetails=new CustomerUserDetails(user);

        return customerUserDetails;
    }
}
