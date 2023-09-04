package com.spring.authentification;

import com.spring.authentification.entity.Role;
import com.spring.authentification.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoginRegistartionApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginRegistartionApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner demo(RoleRepository roleRepo) {
//        return (args) -> {
//            Role role=new Role();
//            role.setName("ADMIN");
//            roleRepo.save(role);
//            role=new Role();
//            role.setName("SEEKER");
//            roleRepo.save(role);
//            role=new Role();
//            role.setName("COMPANY");
//            roleRepo.save(role);
//        };
//    }
}
