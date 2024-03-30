package ru.alitryel.testtinka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alitryel.testtinka.entities.Member;

public interface MemberRepo extends JpaRepository<Member, Long> {
}
