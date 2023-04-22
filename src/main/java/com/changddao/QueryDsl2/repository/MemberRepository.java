package com.changddao.QueryDsl2.repository;

import com.changddao.QueryDsl2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> ,MemberRepositoryCustom{
    // select m from member m where m.username = :username
    List<Member> findByUsername(String username);
}
