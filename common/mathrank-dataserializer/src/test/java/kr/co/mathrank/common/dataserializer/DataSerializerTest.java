package kr.co.mathrank.common.dataserializer;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class DataSerializerTest {
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2020, 1, 1, 1, 1);

    @Test
    void 직렬화() {
        final Person person = new Person("test", 12, DATE_TIME);
        final Optional<String> json = DataSerializer.serialize(person);

        Assertions.assertTrue(json.isPresent());
        log.info(json.toString());
    }

    @Test
    void 역직렬화시_없는필드_무시() {
        final Person person = new Person("test", 12, DATE_TIME);
        final String json = DataSerializer.serialize(person).get();

        final Optional<PersonWithoutAge> dto = DataSerializer.deserialize(json, PersonWithoutAge.class);
        Assertions.assertTrue(dto.isPresent());
        log.info(dto.toString());
    }

    @Test
    void 잘못된_필드_매핑시_필드_널처리() {
        final Person person = new Person("test", 12, DATE_TIME);
        final String json = DataSerializer.serialize(person).get();

        final Optional<WrongPersonDto> dto = DataSerializer.deserialize(json, WrongPersonDto.class);
        Assertions.assertTrue(dto.isPresent());
        Assertions.assertNull(dto.get().birthday1);
        Assertions.assertNull(dto.get().name1);
    }

    private record Person(
            String name,
            Integer age,
            LocalDateTime birthday
    ) {
    }

    private record PersonWithoutAge(
            String name,
//            Integer age,
            LocalDateTime birthday
    ) {
    }

    private record WrongPersonDto(
            String name1,
            LocalDateTime birthday1
    ) {
    }
}
