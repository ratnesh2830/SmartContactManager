package com.smart.DaoClasses;

import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends CrudRepository<Contact,Integer> {

    @Query("from Contact as c where c.user.id=:userID")
    public Page<Contact> getContactByUser(@Param("userID") int userID, Pageable pePageable);

    //search
    public List<Contact> findByNameContainingAndUser(String name, User user);
}
