package com.smart.service;


import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService
{

    public boolean sendEmail(String subject,String message, String to)
    {
        boolean f=false;

        String from="happyratnesh@gmail.com";

        //variable for gmail
        String host="smtp.gmail.com";

        //get system properties
        Properties properties = System.getProperties();

        //set few things in our system properties
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        //to get the session object
        Session session = Session.getInstance(properties, new Authenticator()
                                    {
                                        @Override
                                        protected PasswordAuthentication getPasswordAuthentication()
                                                    {
                                                         return new PasswordAuthentication("happyratnesh@gmail.com","qgsnvyidybuszkcb");
                                                    }
                                    });

        session.setDebug(true);

        //compose the message[text,multimedia]
        MimeMessage m = new MimeMessage(session);

            try{

                //from email
                m.setFrom(from);

                //adding recipient to mimeMessage
                m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));

                //adding subject to mimeMessage
                m.setSubject(subject);

                //adding text/message to mimeMessage
                m.setText(message);

                //sending the message using the Transport class
                Transport.send(m);

                f=true;

            }catch(Exception e)
            {
                e.printStackTrace();
            }

        return f;
    }
}

