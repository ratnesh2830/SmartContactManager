package com.smart.DaoClasses;

import com.smart.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User,Integer>
{
    @Query("Select u from User u where u.email=:email" )
    public User getUserByUserName(@Param("email") String email);
}
