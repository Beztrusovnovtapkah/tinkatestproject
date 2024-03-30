package ru.alitryel.testtinka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alitryel.testtinka.dto.MemberDTO;
import ru.alitryel.testtinka.entities.Member;
import ru.alitryel.testtinka.services.MemberService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberByID(@PathVariable long id) {
        Optional<Member> memberOptional = memberService.getMemberById(id);
        return memberOptional.map(member -> ResponseEntity.ok(mapToDto(member)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> members = memberService.getAllMembers()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }
    private MemberDTO mapToDto(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setNickname(member.getNickname());
        dto.setCountryMember(member.getCountryMember());
        dto.setImageMemberUrl(member.getImageMemberUrl());
        dto.setEloMember(member.getEloMember());
        return dto;
    }

    private Member mapToEntity(MemberDTO dto) {
        Member member = new Member();
        dto.setNickname(member.getNickname());
        dto.setCountryMember(member.getCountryMember());
        dto.setImageMemberUrl(member.getImageMemberUrl());
        dto.setEloMember(member.getEloMember());
        return member;
    }
}
