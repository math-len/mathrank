# 도메인 모듈

## Package Structure
```
+--- Project
|    +--- dto
|    |    +--- command ( class )
|    |    +--- result ( class )
|    +--- service
|    +--- constraint
|    +--- entity
\    +--- repository
```

## Convention
- `@Service`는 `@Validated`을 적용한다.
- `@Service`는 `Command` 객체를 입력으로, `Result` 객체를 응답으로 한다. 
  - `Command`의 필드는 커스텀 `Constraint`를 적용한다.
  - `Result`는 일급 객체만 허용한다.
  - `Result`는 `null`이 될 수 없다.
