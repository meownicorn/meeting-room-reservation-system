package com.jolyn.meetingroomreservationsystem.service.Impl;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import com.jolyn.meetingroomreservationsystem.repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepository.findByEmail(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("Email not found: " + username);
        } else {
            userInfo.setLastLoginDateDisplay(userInfo.getLastLoginDate());
            userInfo.setLastLoginDate(new Date());
            userInfoRepository.save(userInfo);
            log.info("Returning found user by email: " + username);
        }

        return userInfo;
    }
}
