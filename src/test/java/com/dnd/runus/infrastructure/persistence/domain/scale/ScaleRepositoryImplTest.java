package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.scale.Scale;
import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.domain.scale.ScaleSummary;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.scale.entity.ScaleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class ScaleRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ScaleRepository scaleRepository;

    @Transactional
    @DisplayName("지구 한바퀴 코스 조회:코스 수, 전체 코스 거리를 반환한다.")
    @Test
    void getSummary() {
        // given
        // test data insert
        Scale scale1 = new Scale(0, "scale1", 1_000_000, 1, "서울(한국)", "도쿄(일본)");
        Scale scale2 = new Scale(0, "scale2", 2_100_000, 2, "도쿄(일본)", "베이징(중국)");
        Scale scale3 = new Scale(0, "scale3", 1_000_000, 3, "베이징(중국)", "타이베이(대만)");

        em.persist(ScaleEntity.from(scale1));
        em.persist(ScaleEntity.from(scale2));
        em.persist(ScaleEntity.from(scale3));
        em.flush();

        // when
        ScaleSummary summary = scaleRepository.getSummary();

        // then
        assertThat(summary.totalCourseCnt()).isEqualTo(3);
        assertThat(summary.totalCourseDistanceKm()).isEqualTo((4_100 * 1000));
    }
}
