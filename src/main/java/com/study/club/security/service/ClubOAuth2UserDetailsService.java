package com.study.club.security.service;

import com.study.club.entity.ClubMember;
import com.study.club.entity.ClubMemberRole;
import com.study.club.repository.ClubMemberRepository;
import com.study.club.security.dto.ClubAuthMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubOAuth2UserDetailsService extends DefaultOAuth2UserService {

    private final ClubMemberRepository clubMemberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("#### ");
        log.info("userRequest = " + userRequest);

        String clientName = userRequest.getClientRegistration().getClientName();

        log.info("clientName : {}", clientName);
        log.info(String.valueOf(userRequest.getAdditionalParameters()));


        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("#######################################");
        oAuth2User.getAttributes().forEach((k, v) -> {
            log.info(k + " : " + v);
        });

        String email = null;

        if (clientName.equals("Google")) {
            email = oAuth2User.getAttribute("email");
        }

        log.info("### email : {}", email);
        ClubMember clubMember = saveSocialMember(email);

        ClubAuthMemberDto clubAuthMemberDto = new ClubAuthMemberDto(
                clubMember.getEmail(),
                clubMember.getPassword(),
                true,
                clubMember.getRoleSet()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()),
                oAuth2User.getAttributes());

        clubAuthMemberDto.setName(clubMember.getName());

        return clubAuthMemberDto;
    }

    private ClubMember saveSocialMember(String email) {
        Optional<ClubMember> clubMemberOptional = clubMemberRepository.findByEmail(true, email);

        if (clubMemberOptional.isPresent()) {
            return clubMemberOptional.get();
        }

        // 없다면 회원 추가 패스워드는 1111 이름은 그냥 이메일 주소
        ClubMember clubMember = ClubMember.builder()
                .email(email)
                .name(email)
                .password(passwordEncoder.encode("1111"))
                .fromSocial(true)
                .build();
        clubMember.addMemberRole(ClubMemberRole.USER);

        clubMemberRepository.save(clubMember);

        return clubMember;
    }
}
