package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, UUID> {

    Page<History> findByUserEmail(String userEmail, Pageable pageable);


}
