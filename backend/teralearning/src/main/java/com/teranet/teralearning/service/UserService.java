package com.teranet.teralearning.service;

import com.teranet.teralearning.helper.CSVHelper;
import com.teranet.teralearning.model.User;
import com.teranet.teralearning.repository.UserRepository;

import com.teranet.teralearning.util.DateUtility;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.teranet.teralearning.dto.userResponseDTO;
import com.teranet.teralearning.exception.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@NoArgsConstructor
public class UserService extends UserInterface {
    private static Logger log = LoggerFactory.getLogger(UserService.class);
    private DateUtility dateUtility;
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity CreateUser(User user) {
        user.setCreatedDate(dateUtility.getDateTime());
        user.setModifiedDate(dateUtility.getDateTime());
        user.setPassword(encryptPassword("Password1!"));
        return new ResponseEntity(userRepository.save(user), HttpStatus.OK);
    }

    @Override
    public ResponseEntity GetAllUser() {
        return new ResponseEntity(userRepository.findAll(), HttpStatus.OK);
    }

    public List<userResponseDTO> getUserList() throws UserNotFoundException {
        List<userResponseDTO> userResponseDTOS = null;
        try {
            log.debug("UserService:getUserList execution started");
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                return userResponseDTOS;
            } else {
                userResponseDTOS = Collections.emptyList();
                return userResponseDTOS;
            }

        } catch (Exception ex) {
            log.error("Execution occurred while retrieving user list from database");
            throw new UserNotFoundException("Exception occurred while fetch all users from Database");
        }

    }

    public ResponseEntity createMultipleUsers(List<userResponseDTO> userResponseDTOS) {
        log.debug("UserService:createMultipleUsers Init... ");
        try {
            if (!userResponseDTOS.isEmpty()) {
                log.debug("UserService:createMultipleUsers started ");
                int count = 0;
                List<userResponseDTO> preexistentUsers = new ArrayList<>();
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                for (userResponseDTO dto : userResponseDTOS) {
                    User user = new User();
                    user.setFirstName(dto.getFirstname());
                    user.setLastName(dto.getLastName());
                    user.setEmail(dto.getEmail());
                    user.setPhoneNumber(dto.getPhoneNumber());
                    /*user.setStream(); Mapper for Acronym to Stream*/
                    user.setPassword(bCryptPasswordEncoder.encode("Password1!"));
                    user.setUserType(103);
                    user.setCreatedDate(dateUtility.getDateTime());
                    user.setModifiedDate(dateUtility.getDateTime());
                    if (CreateUser(user).getStatusCode() == HttpStatus.OK) {
                        count++;
                        log.info("UserService:createMultipleUsers User Body created for" + user.getEmail());
                    } else {
                        log.info("UserService:createMultipleUsers User already Exist for" + dto.getEmail());
                        preexistentUsers.add(dto);
                    }
                }
                log.info("UserService:createMultipleUsers terminated");
                /*Notification for Created users and PreexistentUSers*/
                return new ResponseEntity("Multiple user created",HttpStatus.CREATED);

            } else {
                return new ResponseEntity("Blank User List", HttpStatus.BAD_REQUEST);
            }


        } catch (Exception ex) {
            log.error("UserService:createMultipleUsers Exception Occurred");
            throw new UserNotFoundException("Exception occurred while fetch user from Database");

        }
    }
    public void CreateUsersFromCSV(MultipartFile file){
        log.info("UserService:CreateUsersFromCSV Init...");
        try{
            log.info("UserService:CreateUsersFromCSV Started");
            List<User> users = CSVHelper.csvToUser(file.getInputStream());
            userRepository.saveAll(users);
        }
        catch(IOException ex){
            log.info("UserService:CreateUsersFromCSV Exception Occurred"+ex.getMessage());
            throw new RuntimeException("Failed to Store CSV Data"+ex.getMessage());
        }
    }
    public void updatePassword(User user, String newPassword){
        try{
            if(user!=null && isUserEmailExists(user.getEmail())){
                user.setPassword(encryptPassword(newPassword));
                //user.setUserStatus(1); Activate Account
                userRepository.save(user);
            }

        }
        catch (Exception ex){
            log.error("UserService:updatePassword: Execution occurred:"+ex);
            throw new UserNotFoundException("Exception occurred while fetch user from Database");
        }
    }
    public boolean isUserEmailExists(String emailID){
        return userRepository.existsByEmail(emailID);
    }
    public User getByUserEmail(String email){
        return userRepository.findByEmail(email);
    }
    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }
    private String encryptPassword(String password){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        return bCryptPasswordEncoder.encode(password);
    }
}
