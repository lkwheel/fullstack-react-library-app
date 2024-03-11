package com.wheelerkode.library.dao;

import com.wheelerkode.library.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
