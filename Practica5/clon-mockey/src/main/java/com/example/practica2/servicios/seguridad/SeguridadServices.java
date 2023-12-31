package com.example.practica2.servicios.seguridad;

import com.example.practica2.entidades.seguridad.Rol;
import com.example.practica2.entidades.seguridad.Usuario;
import com.example.practica2.repositorio.seguridad.RolRepository;
import com.example.practica2.repositorio.seguridad.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SeguridadServices implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    //Para encriptar la información.
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public void crearUsuarioAdmin(){
        System.out.println("Creación del usuario y rol en la base de datos");
        Rol rolAdmin = new Rol("ROLE_ADMIN");
        rolRepository.save(rolAdmin);
        rolRepository.save(new Rol("ROLE_USER"));

        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(bCryptPasswordEncoder.encode("admin"));
        admin.setNombre("Administrador");
        admin.setActivo(true);
        admin.setRoles(new HashSet<>(Arrays.asList(rolAdmin)));
        usuarioRepository.save(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByUsername(username);
        if (user == null){
            System.out.println("ERROR NULL MMG");
        }else{
            System.out.println("encontrado" + user.getUsername());
        }
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        assert user != null;
        for (Rol role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isActivo(), true, true, true, grantedAuthorities);
    }

    public List<Usuario> getUsuarios(){
        return usuarioRepository.findAll();
    }
}
