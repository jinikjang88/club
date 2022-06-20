package com.study.club.controller;

import com.study.club.security.dto.ClubAuthMemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/sample/")
public class SampleController {

    @PreAuthorize("permitAll()")
    @GetMapping("/all")
    public void exAll() {

        // 로그인을 하지 않은 사용자도 접근 할 수 있는
        log.info("### --- ex All " );
    }

    @GetMapping("/member")
    public void exMember(@AuthenticationPrincipal ClubAuthMemberDto clubAuthMemberDto) {
        log.info("exMember.....");

        System.out.println("clubAuthMemberDto = " + clubAuthMemberDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public void exAdmin() {
        // 관리자 권한이 있는 사용자만이 접근 할 수 있는
        log.info("### --- ex Admin " );
    }

    @PreAuthorize("#clubAuthMemberDto != null && #clubAuthMemberDto.username eq \"user95@abc.com\"")
    @GetMapping("/exOnly")
    public String exMemberOnly(@AuthenticationPrincipal ClubAuthMemberDto clubAuthMemberDto) {
        log.info("#################### ");
        log.info(clubAuthMemberDto.toString());
        return "/sample/admin";
    }


}
