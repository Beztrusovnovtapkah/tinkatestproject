package ru.alitryel.testtinka.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alitryel.testtinka.entities.Member;
import ru.alitryel.testtinka.repositories.MemberRepo;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepo memberRepo;

    @Autowired
    public MemberService(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
    }


    public Optional<Member> getMemberById(long id) {

        return memberRepo.findById(id);
    }

    public List<Member> getAllMembers() {

        return memberRepo.findAll();
    }
}
