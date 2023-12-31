package com.example.practica2.config;

import com.example.practica2.servicios.seguridad.SeguridadServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ConfiguracionSeguridad extends WebSecurityConfigurerAdapter {

    //Opción JPA
    @Autowired
    private UserDetailsService userDetailsService;
    //private SeguridadServices userDetailsService;

    //autentificacion de usuarios
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Marcando las reglas para permitir unicamente los usuarios
        http
                .authorizeRequests()
                .antMatchers("/**").hasAnyRole("ADMIN","USER")
                .antMatchers("/usuarios","/verProyectos","/addUser","/modUser","eliminarUser").hasAnyRole("ADMIN")
                .anyRequest().authenticated() //cualquier llamada debe ser validada
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();

        //TODO: validar exclusivamente en ambiente de prueba.
        // deshabilitando las seguridad contra los frame internos.
        //if(!profiles.matches(Pre"prod")){
            //Necesario para H2.
            http.csrf().disable();
            http.headers().frameOptions().disable();
        //}
    }
}
