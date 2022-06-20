package com.study.club.security.service;

import com.study.club.entity.ClubMember;
import com.study.club.repository.ClubMemberRepository;
import com.study.club.security.dto.ClubAuthMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClubUserDetailsService implements UserDetailsService {

    private final ClubMemberRepository clubMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("#### ClubUserDetailsService loadUserByUsername " + username);

        Optional<ClubMember> clubMemberOptional = clubMemberRepository.findByEmail(false, username);
        if (clubMemberOptional.isPresent()) {
            ClubMember clubMember = clubMemberOptional.get();
            log.info("#############################");
            System.out.println("clubMember = " + clubMember);

            ClubAuthMemberDto clubAuthMemberDto = new ClubAuthMemberDto(
                    clubMember.getEmail(),
                    clubMember.getPassword(),
                    clubMember.isFromSocial(),
                    clubMember.getRoleSet().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).collect(Collectors.toSet()));

            clubAuthMemberDto.setName(clubMember.getName());
            clubAuthMemberDto.setFromSocial(clubMember.isFromSocial());

            return clubAuthMemberDto;
        } else {
            throw new UsernameNotFoundException("Check Email Or Social");
        }
    }
}
