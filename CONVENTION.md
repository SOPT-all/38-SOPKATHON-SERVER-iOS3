# 코드 컨벤션

## 1. 패키지 구조

```
org.sopt
├── SopkathonServerApplication.java
├── global
│   ├── code
│   │   ├── ApiCode.java          # 최상위 인터페이스
│   │   ├── SuccessCode.java      # 성공 코드 인터페이스
│   │   ├── ErrorCode.java        # 에러 코드 인터페이스
│   │   ├── GlobalSuccessCode.java
│   │   └── GlobalErrorCode.java
│   ├── config                    # Spring 설정 클래스
│   ├── entity
│   │   └── BaseTimeEntity.java
│   ├── exception
│   │   ├── BaseException.java
│   │   └── GlobalExceptionHandler.java
│   └── response
│       └── CommonApiResponse.java
└── domain
    └── {domainName}              # 예: mistake, user, reflection
        ├── service
        │   └── MistakeService.java
        ├── repository
        │   └── MistakeRepository.java
        ├── entity
        │   └── Mistake.java
        ├── dto
        │   ├── request
        │   │   └── MistakeCreateRequest.java
        │   └── response
        │       └── MistakeResponse.java
        ├── controller
        │   ├── MistakeController.java
        │   └── MistakeApi.java           # Swagger 전용 인터페이스
        └── exception
            ├── MistakeErrorCode.java
            ├── MistakeNotFoundException.java       # ErrorCode 1개당 Exception 1개
            └── MistakeAccessDeniedException.java
```

---

## 2. 엔티티 (Entity)

```java
@Getter
@Entity
@Table(name = "mistakes")               // 복수형 스네이크케이스
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mistake extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mistake_id")        // 엔티티명_id 형태
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 연관관계는 항상 LAZY
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    private Mistake(User user, String content) {  // private 생성자
        this.user = user;
        this.content = content;
    }

    public static Mistake create(User user, String content) {  // 정적 팩토리 메서드
        return new Mistake(user, content);
    }

    // 상태 변경은 도메인 메서드로
    public void updateContent(String content) {
        this.content = content;
    }
}
```

**규칙**
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 필수
- 생성자는 `private`, 외부 생성은 `create()` 정적 팩토리 메서드 사용
- `@Setter` 사용 금지 — 상태 변경은 의미 있는 도메인 메서드로 표현
- 연관관계는 `FetchType.LAZY` 기본
- `@Column(name = ...)` PK는 `{엔티티명}_id` 형태로 명시
- `BaseTimeEntity` 상속으로 `createdAt` 자동 관리

---

## 3. 레포지토리 (Repository)

```java
public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    List<Mistake> findByUserOrderByDateDesc(User user);

    // 조회 실패 시 예외를 던지는 헬퍼 메서드는 레포지토리에 두지 않음
    // 서비스에서 orElseThrow() 처리
}
```

**규칙**
- `JpaRepository<Entity, PK타입>` 상속
- 복잡한 쿼리는 `@Query` 사용
- 레포지토리에서 예외를 던지지 않음 — 서비스에서 `orElseThrow()` 처리

---

## 4. 서비스 (Service)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)         // 클래스 레벨 readOnly = true
public class MistakeService {

    private final MistakeRepository mistakeRepository;
    private final UserRepository userRepository;

    @Transactional                       // 쓰기 작업에만 개별 @Transactional 추가
    public MistakeResponse create(Long userId, MistakeCreateRequest request) {
        User user = getUserOrThrow(userId);
        Mistake mistake = Mistake.create(user, request.content(), request.date());
        mistakeRepository.save(mistake);
        return MistakeResponse.from(mistake);
    }

    public MistakeResponse getById(Long mistakeId) {
        Mistake mistake = getMistakeOrThrow(mistakeId);
        return MistakeResponse.from(mistake);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Mistake getMistakeOrThrow(Long mistakeId) {
        return mistakeRepository.findById(mistakeId)
                .orElseThrow(MistakeNotFoundException::new);
    }
}
```

**규칙**
- 클래스 레벨에 `@Transactional(readOnly = true)` 선언
- 데이터 변경 메서드에만 `@Transactional` 개별 추가
- 인터페이스 없이 구현체 클래스(`XxxService`)만 사용
- `orElseThrow()` 헬퍼는 `private` 메서드로 하단에 모아서 관리
- 서비스는 DTO ↔ 엔티티 변환 책임을 가짐

---

## 5. 컨트롤러 (Controller)

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mistakes")
public class MistakeController implements MistakeApi {  // Swagger 인터페이스 구현

    private final MistakeService mistakeService;

    @PostMapping
    public ResponseEntity<CommonApiResponse<MistakeResponse>> create(
            @Valid @RequestBody MistakeCreateRequest request
    ) {
        MistakeResponse response = mistakeService.create(request);
        return CommonApiResponse.successResponse(GlobalSuccessCode.CREATED, response);
    }

    @GetMapping("/{mistakeId}")
    public ResponseEntity<CommonApiResponse<MistakeResponse>> getById(
            @PathVariable Long mistakeId
    ) {
        MistakeResponse response = mistakeService.getById(mistakeId);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }

    @DeleteMapping("/{mistakeId}")
    public ResponseEntity<CommonApiResponse<Void>> delete(
            @PathVariable Long mistakeId
    ) {
        mistakeService.delete(mistakeId);
        return CommonApiResponse.successResponse(GlobalSuccessCode.NO_CONTENT, null);
    }
}
```

**규칙**
- URL은 복수형 명사, kebab-case (`/v1/mistakes`) — `/api` prefix는 사용하지 않음
- `@Valid` 파라미터 레벨에만 사용
- `{Domain}Api` 인터페이스를 `implements` — Swagger 어노테이션은 컨트롤러에 작성 금지
- 컨트롤러는 요청/응답 처리만 담당 — 비즈니스 로직 금지
- 반환 타입은 항상 `ResponseEntity<CommonApiResponse<T>>`
- PathVariable 이름은 `{엔티티명Id}` 형태 (`{mistakeId}`)

---

## 6. DTO

```java
// Request — record 사용, validation 어노테이션 포함
public record MistakeCreateRequest(
        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @NotNull(message = "날짜는 필수입니다.")
        LocalDate date
) {}

// Response — record 사용, 정적 팩토리 메서드 from()
public record MistakeResponse(
        Long id,
        String content,
        LocalDate date
) {
    public static MistakeResponse from(Mistake mistake) {
        return new MistakeResponse(
                mistake.getId(),
                mistake.getContent(),
                mistake.getDate()
        );
    }
}
```

**규칙**
- Request, Response 모두 `record` 사용
- Request: validation 어노테이션(`@NotBlank`, `@NotNull` 등) + 한국어 `message` 필수
- Response: 엔티티를 받아 변환하는 `from(Entity entity)` 정적 팩토리 메서드 사용
- DTO에서 엔티티를 직접 참조하지 않음 (Response의 `from()`은 허용)
- 파일명: `{역할}{동사}Request.java`, `{역할}Response.java`
  - `MistakeCreateRequest`, `MistakeListResponse`, `MistakeDetailResponse`

---

## 7. 에러 코드 (ErrorCode)

```java
// domain/mistake/exception/MistakeErrorCode.java
@Getter
@RequiredArgsConstructor
public enum MistakeErrorCode implements ErrorCode {

    MISTAKE_NOT_FOUND("MST-E001", HttpStatus.NOT_FOUND, "해당 실수 기록을 찾을 수 없습니다."),
    MISTAKE_ACCESS_DENIED("MST-E002", HttpStatus.FORBIDDEN, "해당 실수 기록에 접근할 권한이 없습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
```

**규칙**
- 도메인별 `enum`으로 관리, `ErrorCode` 인터페이스 구현
- 도메인 패키지 내 `exception/` 하위에 위치
- 코드 형식: `{도메인 약어 대문자}-E{3자리 숫자}` (예: `MST-E001`, `USR-E001`)
- 성공 코드는 `GlobalSuccessCode`만 사용 (도메인별 성공 코드 추가 금지)

---

## 8. 도메인 예외 클래스 (Domain Exception)

ErrorCode 값 하나당 `BaseException`을 상속하는 예외 클래스를 하나씩 생성한다.

```java
// domain/mistake/exception/MistakeNotFoundException.java
public class MistakeNotFoundException extends BaseException {

    public MistakeNotFoundException() {
        super(MistakeErrorCode.MISTAKE_NOT_FOUND);
    }
}

// domain/mistake/exception/MistakeAccessDeniedException.java
public class MistakeAccessDeniedException extends BaseException {

    public MistakeAccessDeniedException() {
        super(MistakeErrorCode.MISTAKE_ACCESS_DENIED);
    }
}
```

**규칙**
- `BaseException`을 상속하고, no-args 생성자에서 `super(ErrorCode)`를 호출
  - `ApiExceptionsOperationCustomizer`가 리플렉션으로 no-args 생성자를 호출해 ErrorCode를 추출하기 때문
- 파일명: `{ErrorCode 상수명을 PascalCase로}Exception.java`
  - `MISTAKE_NOT_FOUND` → `MistakeNotFoundException`
  - `MISTAKE_ACCESS_DENIED` → `MistakeAccessDeniedException`
- 도메인 패키지 내 `exception/` 하위에 ErrorCode와 함께 위치
- 예외 발생 시 구체적인 예외 클래스를 사용: `throw new MistakeNotFoundException()`

---

## 9. Swagger API 인터페이스 ({Domain}Api)

컨트롤러의 Swagger 어노테이션을 분리하기 위한 전용 인터페이스를 `controller/` 패키지에 생성한다.

```java
// domain/mistake/controller/MistakeApi.java
@Tag(name = "Mistake", description = "실수 기록 API")
public interface MistakeApi {

    @Operation(summary = "실수 기록 생성")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiExceptions({MistakeNotFoundException.class, MistakeAccessDeniedException.class})
    ResponseEntity<CommonApiResponse<MistakeResponse>> create(
            @RequestBody MistakeCreateRequest request
    );

    @Operation(summary = "실수 기록 단건 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiExceptions({MistakeNotFoundException.class})
    ResponseEntity<CommonApiResponse<MistakeResponse>> getById(
            @PathVariable Long mistakeId
    );

    @Operation(summary = "실수 기록 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiExceptions({MistakeNotFoundException.class})
    ResponseEntity<CommonApiResponse<Void>> delete(
            @PathVariable Long mistakeId
    );
}
```

**규칙**
- 파일명: `{Domain}Api.java`, `controller/` 패키지에 위치
- `@Tag`, `@Operation`, `@ApiResponse`, `@ApiExceptions` 등 모든 Swagger 어노테이션은 이 인터페이스에만 작성
- `@ApiExceptions`에는 해당 메서드에서 발생할 수 있는 도메인 예외 클래스를 모두 등록
- 컨트롤러는 이 인터페이스를 `implements`하고 Swagger 어노테이션을 중복 작성하지 않음
- 인터페이스 메서드에는 `@Valid` 미포함 — 컨트롤러의 구현체에서 파라미터에 `@Valid` 선언

---

## 10. 사용자 인식 (User-Id 헤더)

인증/인가 없이 HTTP 헤더 `User-Id`로 요청자를 식별한다.

```java
// Controller — 헤더에서 userId를 받아 서비스에 전달
@GetMapping("/{mistakeId}")
public ResponseEntity<CommonApiResponse<MistakeResponse>> getById(
        @RequestHeader("User-Id") Long userId,
        @PathVariable Long mistakeId
) {
    return CommonApiResponse.successResponse(GlobalSuccessCode.OK, mistakeService.getById(userId, mistakeId));
}

// Service — userId로 User 엔티티를 조회한 뒤 비즈니스 로직 수행
public MistakeResponse getById(Long userId, Long mistakeId) {
    User user = getUserOrThrow(userId);
    Mistake mistake = getMistakeOrThrow(mistakeId);
    ...
}
```

**규칙**
- 헤더명은 `User-Id` (고정)
- 컨트롤러 파라미터: `@RequestHeader("User-Id") Long userId`
- `userId`는 컨트롤러에서 서비스로 전달하고, 서비스에서 `getUserOrThrow()`로 `User` 엔티티를 조회
- `User-Id` 헤더가 없거나 유효하지 않으면 `GlobalExceptionHandler`가 자동으로 400 처리
- `{Domain}Api` 인터페이스의 메서드 시그니처에도 동일하게 `@RequestHeader("User-Id") Long userId` 포함

---

## 11. Validation

- `@Valid`: `@RequestBody` 요청 본문 검증 시 파라미터 레벨에 사용
- validation 어노테이션은 DTO Record에 선언, 서비스에서 직접 검증 로직 작성 금지
- 커스텀 검증이 필요한 경우 서비스에서 `BaseException` throw

---

## 12. 네이밍 규칙 요약

| 대상 | 규칙 | 예시 |
|---|---|---|
| 클래스 | PascalCase | `MistakeService` |
| 메서드, 변수 | camelCase | `getMistakeOrThrow` |
| 상수 | UPPER_SNAKE_CASE | `MISTAKE_NOT_FOUND` |
| DB 테이블 | 복수형 snake_case | `mistakes`, `user_mistakes` |
| DB 컬럼 | snake_case | `image_url`, `created_at` |
| URL | 복수형 kebab-case | `/v1/mistakes`, `/v1/user-mistakes` |
| Request DTO | `{역할}{동사}Request` | `MistakeCreateRequest` |
| Response DTO | `{역할}Response` | `MistakeDetailResponse` |
| ErrorCode 코드값 | `{약어}-E{3자리숫자}` | `MST-E001` |
| 도메인 예외 클래스 | `{ErrorCode명 PascalCase}Exception` | `MistakeNotFoundException` |
| Swagger 인터페이스 | `{Domain}Api` | `MistakeApi` |