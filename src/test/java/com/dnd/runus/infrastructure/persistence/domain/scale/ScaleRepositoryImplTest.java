package com.dnd.runus.infrastructure.persistence.domain.scale;

import com.dnd.runus.domain.scale.ScaleRepository;
import com.dnd.runus.domain.scale.ScaleSummary;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static com.dnd.runus.global.constant.ScaleConstant.DISTANCE_KM_AROUND_THE_EARTH;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class ScaleRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ScaleRepository scaleRepository;

    @Transactional
    @DisplayName("지구 한바퀴 코스 조회:코스 수, 전체 코스 거리를 반환한다.")
    @Test
    void getSummary() {
        // given
        // test data insert
        String insertSql =
                """
            insert into scale (name, size_meter, start_name, end_name, index)
            values (?, ?, ?, ?, ?),
            (?, ?, ?, ?, ?),
            (?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(
                insertSql,
                "scale1",
                1_000_000,
                "서울(한국)",
                "도쿄(일본)",
                1,
                "scale2",
                2_100_000,
                "도쿄(일본)",
                "베이징(중국)",
                2,
                "scale3",
                1_000_000,
                "베이징(중국)",
                "타이베이(대만)",
                3);

        // when
        ScaleSummary summary = scaleRepository.getSummary();

        // then
        assertThat(summary.totalCourseCnt()).isEqualTo("3코스");
        assertThat(summary.earthDistanceKm()).isEqualTo(DISTANCE_KM_AROUND_THE_EARTH);
        assertThat(summary.totalCourseDistanceKm()).isEqualTo("4,100km");
    }
}
