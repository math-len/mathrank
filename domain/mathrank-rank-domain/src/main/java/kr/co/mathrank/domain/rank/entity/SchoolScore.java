package kr.co.mathrank.domain.rank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchoolScore {
    @Id
    @GeneratedValue
    private Long id;

    private String schoolCode;

    @Version
    private Long version = 0L;

    private Long score = 0L;

    public void addScore(final Integer score) {
        this.score += score;
    }

    public static SchoolScore create(final String schoolCode) {
        final SchoolScore schoolScore = new SchoolScore();
        schoolScore.schoolCode = schoolCode;
        return schoolScore;
    }
}
