package com.spring.service.security.impl;

import com.spring.exception.NotParsableContentException;
import com.spring.exception.ResourceNotFoundException;
import com.spring.model.Accounts;
import com.spring.model.security.JwtUserFactory;
import com.spring.repository.AccountRepository;
import com.spring.security.oauth2.UserPrincipal;
import com.spring.service.account.AccountService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Accounts> account = this.accountService.checkIfEmailExistsAndDeletedAt(email);

        if (account.isPresent()) {
            return JwtUserFactory.create(account.get());
        }
        throw new UsernameNotFoundException("User/Email not found.");
    }

    @Transactional
    public UserDetails loadUserById(Long id)  {
//        System.out.println("vaof day");
       Accounts user = this.accountRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }


}
