package com.ScoreYourFood.demo;

import com.ScoreYourFood.demo.Services.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ScoreYourFoodApplication {
    
        @Autowired
        private UsuarioServices usuarioServicio;

	public static void main(String[] args) {
		SpringApplication.run(ScoreYourFoodApplication.class, args);
	}
        
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
            auth.userDetailsService(usuarioServicio).passwordEncoder(new BCryptPasswordEncoder());
        }

}
