package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Page<History> findByUserEmail(String userEmail, Pageable pageable);


}
