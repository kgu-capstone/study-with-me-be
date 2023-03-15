package com.kgu.studywithme.post.domain;

import com.kgu.studywithme.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository  extends JpaRepository<Post, Long> {
}
