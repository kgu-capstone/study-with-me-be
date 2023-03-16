package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository  extends JpaRepository<Study, Long> {
    /*
    save(S)
        새로운 엔티티는 저장하고, 이미 있는 엔티티는 수정
        내부에서 식별자 값이 없으면 em.persist(), 있으면 em.merge() 호출
    delete(T)
        엔티티 하나 삭제
        내부에서 em.remove() 호출
    findByOne(id)
        엔티티 1개를 id 값으로 조회
        내부에서 em.find() 호출
    getOne(id)
        엔티티를 프록시로 조회
        내부에서 em.getReference() 호출
    findAll()
        모든엔티티 조회 (sort, pagable 조건을 파라미터로 제공)
     */

    List<Study> findAllByCategory(@Param("category")Category category);
}
