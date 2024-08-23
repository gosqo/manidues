> 문서 업데이트: 2024-08-20

## 목차

1. [소개](#소개-intro)
2. [사용 기술](#사용-기술-skills)
3. [구조](#구조-architecture)
4. [개체간 관계도](#개체간-관계도-erd)
5. [기능](#기능-features)
   1. [Application](#application)
   2. [Web Server](#web-server)
6. [테스트](#테스트-test)
7. [개선의 경험](#개선의-경험-experience-of-improvement)
   1. [테스트 환경 개선](#테스트-환경-개선)
      1. [속도 개선](#속도-개선)
      2. [테스트 데이터 생명 주기 관리 전략](#테스트-데이터-생명-주기-관리-전략)
8. [해프닝](#해프닝)

소개 (Intro)
---
회원, 인증, 게시물, 댓글 기반 게시판 서비스로, 개인 풀스택 프로젝트입니다.   
REST 구조에 따라 API 를 설계, 구현했습니다.   
<sup>2024.02 - 현재 진행 중.</sup>   
CommentLike 관련 기능 추가 중

[서비스 이용해 보기](https://flyin-heron.duckdns.org)

사용 기술 (Skills)
---

* Back-End: Java, Spring Boot, Spring Data JPA
* Database: MySQL
* DevOps: Nginx, Git
* Front-End: HTML, CSS, BootStrap, JavaScript

구조 (Architecture)
---
![architecture](./assets/img/20240806_architecture.png)

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

개체간 관계도 (ERD)
---
![erd](./assets/img/20240820_erd.png)

CommentLike 관련 기능 추가 중

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

기능 (Features)
---

### Application

##### Entity

* 회원 <small>member</small>
  - 가입, 로그인
    - 가입 시, 각 `<input>` 유효성 검사.
    - 모든 조건을 만족하는지 사용자 입력마다 반응해 `<submit>` 활성화. (서버에서 2차 검증)
  - 게시물, 댓글 '조회' 외의 기능에 대한 인가. `@PreAuthorize`

* 토큰 <small>token</small>
  - 매일 특정 시간에 Database 조회, 만료된 Refresh token 삭제 및 로그아웃 처리.
  - 인가가 필요한 end-point 요청 시, 헤더에 Access token 을 담아 요청.
    - 만료되지 않은 경우, 인가 및 요청 처리.
    - 만료된 경우 ,리프레시 토큰으로 재요청, Database 조회,
      - 유효한 Refresh token 이고,
        - 이 토큰의 만료시점 까지 7일 넘게 남았다면 Access token 재발급.
        - 7일 이내이면 Access / Refresh token 재발급.

* 게시물 <small>board</small>
  - 목록 페이지네이션 (`Page`).
  - 등록, 조회, 수정, 삭제.
  - 쿠키 기반 조회수 관리.

* 댓글 <small>comment</small>
  - 목록 더 불러오기 (`Slice`).
  - 등록, 조회, 수정, 삭제.
  - ~~좋아요 기능~~(추가 예정)

##### Global

* 예외 처리
  - 서비스 중 일어날 수 있는 예외를 대비해, 상황에 알맞는 HTTP 상태 코드와 메시지를 본문에 담아 응답.    
    ---> 정상 응답 `DTO` 에도 포함 된 필드로, 프론트 측 서버 API 호출 메서드 재사용에 효과적.

* 필터 계층
  - WAS 로 향하는 HTTP 요청 로깅.
  - JWT 인증 시 일어날 수 있는 `JwtException` 처리하는 객체 등록.

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

### Web Server

##### Nginx

* 요청 헤더 `User-Agent`, `Connection` 기반 최소한의 무차별 / 비정상 요청에 `444` 상태 코드 응답.
* 정적 자원 반환 및 `Cache-Control` 지시.
* 정적 자원을 제외한, 서버로 향하는 모든 접근 로깅.

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

테스트 (Test)
---

Controller, Service, Repository 계층별 / 통합 테스트. 다음의 경우에 유용하게 사용 중입니다.

* 신규 기능 도입 시 기능의 작동 원리, 구조에 대한 지식이 확실하지 않은 부분의 확인.
* 기능 구현, 리팩토링 후, 의도대로 작동하는 지 확인.

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

개선의 경험 (Experience of Improvement)
---

### 테스트 환경 개선

#### 속도 개선

기능 개발과 코드 리팩토링에 테스트가 주는 안정감을 느끼고 적극 이용 중입니다.

![test improvement](./assets/img/20240806_test_improvement.png)

##### 배경

- 여러 테스트를 묶어 실행 시, `id` 생성 전략에 따른 `id` 값의 증가로 리터럴 `id` 를 통한 엔티티 참조 시, `NoSuchElementException` 발생하는 문제.
- 이전 문제를 해결하고자 `@DirtiesContext` 애너테이션 도입 후, 테스트 소요 시간 증가.

##### 해결 방안

- 상속을 통해 상위 클래스 필드로 선언 및 초기화한 엔티티를 통해 `id` 접근.
- 상위 클래스에서의 테스트 데이터 초기화 관장. 및 `@DirtiesContext` 제거

##### 결과

- 상속을 통해 데이터 초기화, 인스턴스 생성 등 공통적 요소를 효과적으로 관리할 수 있게 됐습니다.
- 테스트 실행 순서에 상관없이 초기화된 데이터를 참조할 수 있게 됐습니다.
- 컨텍스트 로드 횟수를 1회로 줄여, **테스트 수행 시간을 63% 정도 단축**할 수 있었습니다.

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

#### 테스트 데이터 생명 주기 관리 전략

##### 배경

현재 프로젝트에는 총 87 개의 테스트가 존재합니다.
이들 중, 데이터베이스에 의존하고, 데이터를 필요로 하는 테스트는 39 개입니다.
이 테스트들이 사용하는 데이터의 생명주기 관리 코드를 효율적으로 관리하고자 했습니다.

##### 해결 방안

* 테스트 데이터로 사용할 **엔티티 생성 역할**을 담당하는 최상위 추상 클래스 선언.
  - 생성한 **데이터를 저장하는 행위를 추상화**.
* 최상위 클래스를 상속하여, **계층별 데이터 저장 주체에 따라** 각각 데이터 저장 행위의 구현.
* 테스트 클래스가 사용하는 **컨텍스트에 따라**, 상속한 필드, 데이터 저장 행위를 통한 데이터 초기화.

##### 문제 개선 이후, 테스트 클래스 상속 구조

![test data initializer dependencies](./assets/img/20240820_test_data_initializer_dependencies.png)

<small style="text-align: center; display: block;"></small>

1. `TestDataInitializer` (데이터 생성)
   - 테스트에 필요한 데이터를 생성하는 역할을 수행합니다.
   - 생성한 데이터를 데이터베이스에 저장하는 행위를 추상화한 명세를 제공합니다.
2. `EntityManagerDataInitializer`, `RepositoryDataInitializer` (데이터 저장 주체 분리)
   - `TestDataInitializer` 를 상속받는 두 객체는 상위 객체에서 생성한 데이터를 데이터베이스에 저장하는 행위를 각각 `EntityManager`, `JpaRepository`를 통해 구현합니다.
3. `DataJpaTestRepositoryDataInitializer`, `SpringBootTestBase` (컨텍스트 분리)
   - `JpaRepository` 를 사용해 데이터를 저장하는 경우는 두 가지의 컨텍스트로 나누어집니다.
     - 데이터베이스 관련으로 한정된 컨텍스트
     - 앱 전체 컨텍스트
   - 상위 객체가 제공하는 데이터 저장 행위를 각각 다른 컨텍스트에서 사용할 수 있습니다.

위와 같이 역할에 따라 계층을 분리하고, 용도에 따라 구현을 분리,
테스트 클래스가 요구하는 각 맥락에서 필요한 상위 클래스를 상속해, 데이터 저장 메서드를 호출하면,
테스트 데이터 생명주기를 효율적으로 관리할 수 있습니다.

#### 결과

각 클래스가 가지는 역할이 명확해짐으로 코드의 관리가 용이해졌습니다.

* 각 계층별 상위 클래스에서 담당해 3 곳으로 흩어져있던 엔티티 생성 코드를 1개의 클래스에서 담당하게 됐습니다.
* `JpaRepository`, `EntityManager`를 통한 영속화 메서드를 오버라이딩 해,
  해당 클래스를 상속하면 `...Repository,` `entityManager` 필드를 사용할 수 있습니다.
* 테스트 클래스에서는 데이터 초기화, 데이터 관련 필드의 코드 없이 테스트 코드에 집중할 수 있습니다.

#### 알게 된 점

* 엔티티 테스트 중, 엔티티의 상위 클래스 protected 메서드에 접근하기 위해 패키지 구조를 조정할 필요가 있었습니다.
  이로 인해 접근 제어자와 프로덕션-테스트 패키지 구조를 동일하게 가져가는 것의 이유를 체감하게 됐습니다.
* `@SpringBootTest` 애너테이션의 경우, `SpringBootTest.WebEnvironment.RANDOM_PORT`로 지정한 여러 테스트를 묶어 실행할 때,
  테스트 마다 Bean 으로 등록된 인스턴스를 `final`로 선언하더라도 컨텍스트 로딩 시 등록한 인스턴스를 공유하는 것을 알 수 있었습니다.

이에 스프링의 구조에 대해 관심 갖고 학습 중입니다.

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>

### @DataJpaTest 에 Service 가져오기

서비스 클래스 중, `Page`, `Slice` 의 `content` 가 비어있다면 예외를 던지는 메서드 테스트할 때,   
`JpaRepository` 를 상속한 인터페이스가 반환하는 `Page`, `Slice` 를 직접 구현해야 하는 조금은 번거로운 문제가 있었습니다.   
--> 이를 해결하기 위해 `@DataJpaTest`, `@Import(TargetService.class)` 를 적용,
JPA 가 반환하는 `Page`, `Slice` 를 사용해 간결한 테스트를 작성할 수 있었습니다.

### 상속 활용하기

테스트 코드에 친숙해지며, 상속의 이점을 체감 후, 신규 기능 도입에도 적용,   
`BaseEntity`를 추상 클래스로 등록, 엔티티가 기본적으로 가지는 필드를 관리.   
엔티티 상태 초기화는 엔티티 성격에 따라 달라지므로 추상 메서드로 선언하고, 하위 클래스에서 구현.

### Shell Script 작성을 통한 1줄 배포

생각보다 잦은 프로젝트 업데이트에 단순 반복적 배포 작업을 자동화.   
--> 배포 서버의 Shell Script 를 통한 패치, 빌드, 배포.

해프닝
---

* 의도와 다르게 삭제된 배포 서버 데이터를 MySQL binlog 기반 복구한 경험이 있습니다.
  - 삭제 원인은 충분치 않은 테스트 케이스.
  - 이 경험을 통해 테스트 작성 시, 하나의 성공에 대해 반대 케이스의 중요성을 인식.
  - `.bat` 스크립트를 작성 해 데이터베이스 데이터, 로그의 백업.

<div dir="rtl">
  <a href="#목차">목차로 돌아가기</a>
</div>
