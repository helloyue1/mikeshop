//package com.example.demo.repository;
//
//import com.example.demo.entity.User;
//import com.example.demo.mapper.UserMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserRepository extends JpaRepository<> {
//    private final UserMapper userMapper;
//
//    @Autowired
//    public UserRepository(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }
//
//    public User userLogin(String username, String password) {
//        return userMapper.selectByUsernameAndPassword(username, password);
//    }
//}
