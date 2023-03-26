package com.jolyn.meetingroomreservationsystem.repository;

import com.jolyn.meetingroomreservationsystem.domain.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    UserInfo findByEmail(String email);
    boolean existsById(String id);
    void deleteById(String id);
    List<UserInfo> findAll(Specification<UserInfo> spec);
    boolean existsByEmail(String email);
}
