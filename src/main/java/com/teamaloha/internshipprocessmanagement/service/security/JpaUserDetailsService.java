package com.teamaloha.internshipprocessmanagement.service.security;

import com.teamaloha.internshipprocessmanagement.dto.security.SecurityUser;
import com.teamaloha.internshipprocessmanagement.dto.user.UserDto;
import com.teamaloha.internshipprocessmanagement.entity.User;
import com.teamaloha.internshipprocessmanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);

    @Autowired
    public JpaUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Cacheable(value = "users", sync = true)
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = userService.findByMail(mail);
        if (user == null) {
            String errorMessage = "User is not found. Mail: " + mail;
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return new SecurityUser(userDto);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Scheduled(fixedDelayString = "${caching.spring.userCacheTTL}")
    public void emptyUsersCache() {
        logger.info("Emptying Users cache");
    }
}
