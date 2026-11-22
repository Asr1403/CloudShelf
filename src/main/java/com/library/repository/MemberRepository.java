
package com.library.repository;

import com.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // Search members by name or email
    List<Member> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email);

    // Check whether an email is already registered
    boolean existsByEmailIgnoreCase(String email);

    // Retrieve members alphabetically
    List<Member> findAllByOrderByNameAsc();
}
