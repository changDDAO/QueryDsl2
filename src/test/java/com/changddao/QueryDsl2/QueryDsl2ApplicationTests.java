package com.changddao.QueryDsl2;

import com.changddao.QueryDsl2.entity.Hello;
import com.changddao.QueryDsl2.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
@Commit
class QueryDsl2ApplicationTests {
	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
	}

	@Test
	public void simpleTest() throws Exception {
		//given
		Hello hello = new Hello();
		em.persist(hello);
		JPAQueryFactory queryFactory = new JPAQueryFactory(em);
		QHello qHello = new QHello("h");


		//when
		Hello hello1 = queryFactory.selectFrom(qHello)
				.fetchOne();

		//then
		Assertions.assertThat(hello).isEqualTo(hello1);

	}

}
