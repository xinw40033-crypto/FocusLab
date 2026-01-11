package com.focuslab.common.repository;

import com.focuslab.common.entity.TrainingContent;
import com.focuslab.common.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainingContentRepository extends JpaRepository<TrainingContent, Long> {
    List<TrainingContent> findByType(TrainingType type);
    
    List<TrainingContent> findByDifficulty(Integer difficulty);
    
    List<TrainingContent> findByTypeAndDifficulty(TrainingType type, Integer difficulty);
}