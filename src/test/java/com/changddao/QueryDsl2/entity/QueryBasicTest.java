package com.changddao.QueryDsl2.entity;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import java.util.List;

import static com.changddao.QueryDsl2.entity.QMember.*;
import static com.changddao.QueryDsl2.entity.QTeam.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QueryBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;


    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);
        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.flush();
        em.clear();
    }

    @Test
    public void startJPQL() throws Exception {
        //given
        //member1을 찾아라
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        //when

        //then
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }
    @Test
    public void startQuerydsl(){

        //given

        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();


        //when
        //then
        assertThat(findMember.getUsername()).isEqualTo("member1");


    }

    @Test
    /*
    * 회원 나이 내림차순
    * 회원 이름 오름차순
    * 회원 이름이 없으면 마지막에 널출력*/
    public void sort(){

    //given
    em.persist(new Member(null,100));
    em.persist(new Member("member5",100));
    em.persist(new Member("member6",100));


    //when
        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();


        //then
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }
    @Test
    public void paging1 (){
    //given
    List<Member> result = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetch();
    assertThat(result.size()).isEqualTo(2);



    //when



    //then


    }
    @Test
    public void paging2 (){
    //given
        QueryResults<Member> result = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(result.getResults().size()).isEqualTo(2);
        assertThat(result.getTotal()).isEqualTo(4);

        //when



    //then


    }
    @Test
    public void aggregation(){
    //given
        List<Tuple> fetch = queryFactory.select(member.count(),
                        member.age.avg(),
                        member.age.sum())
                .from(member)
                .fetch();
        Tuple tuple = fetch.get(0);//when
        assertThat(tuple.get(member.count())).isEqualTo(4);


            //then


    }
    @Test
    public void group(){
    //given
        List<Tuple> result = queryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();


        //when
     Tuple teamA = result.get(0);
     Tuple teamB = result.get(1);


    //then
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamB.get(team.name)).isEqualTo("teamB");

        assertThat(teamA.get(member.age.avg())).isEqualTo(15);



    }
    @Test
    public void join(){
    //given
        List<Member> result = queryFactory.select(member)
                .from(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();


        //when
            assertThat(result).extracting("username")
                    .containsExactly("member1","member2");


    //then
    }
    // 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
    @Test
    public void join_on_filtering(){
    //given
        List<Tuple> members = queryFactory.select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();


        //when
        for(Tuple tuple : members){
            System.out.println("tuple = " + tuple);
        }


    //then
    }
    @PersistenceUnit
    EntityManagerFactory emf;
    @Test
    public void fetchJoinNo(){

    //given
    em.flush();
    em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).isFalse(); //페치조인 미적용




        //when



    //then


    }
    @Test
    public void fetchJoin() {

        //given
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team,team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).isTrue();
    }

    @Test
    public void subQuery(){
    //given



    //when



    //then


    }


}
