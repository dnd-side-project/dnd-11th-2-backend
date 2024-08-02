package com.dnd.runus.infrastructure.persistence.jpa.running;

import com.dnd.runus.infrastructure.persistence.jpa.running.entity.RunningEmojiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRunningEmojiRepository extends JpaRepository<RunningEmojiEntity, Long> {}
