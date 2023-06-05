//package com.example.blogservice.service;
//
//import com.example.blogservice.entity.User;
//import com.example.blogservice.repository.UserRepository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//
//public class UserDetailService implements UserDetailsService {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUserName(username)
//                                        .orElseThrow(() -> new UsernameNotFoundException("User not found "));
//        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
//                                        new ArrayList<>());
//    }
//}
