# proj_mini_now-emo
Spring Boot backend + Vue 3 frontend emotion logging project

# emotion-snapshot

Java 17, Spring Boot 3.x 미니 앱. 감정 로그 CRUD와 일/주/월/연 통계 API, Chart.js 기반 단일 페이지 UI 제공.

## 실행 (백엔드)

- (로컬 Maven 사용)

```bash
mvn spring-boot:run
```

- 브라우저:
  - 앱: http://localhost:8080/
  - H2 콘솔: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:emotiondb`)

## 주요 엔드포인트

- POST `/api/emotions` {"level":1..5}
- GET `/api/emotions?from&to` (ISO8601 Instant)
- PATCH `/api/emotions/{id}` {"level":1..5}
- DELETE `/api/emotions/{id}`
- GET `/api/stats?range=daily|weekly|monthly|yearly`
- (옵션) GET `/actuator/health`

## 시간대/DB

- JDBC 타임존: Asia/Seoul (UTC 저장/조회 일관성)
- H2 메모리 DB(dev용), 콘솔 활성화

## Seed 데이터

- `src/main/resources/data.sql`에서 3~5개 초기 데이터 삽입

## 빠른 테스트

```bash
curl -X POST localhost:8080/api/emotions -H "Content-Type: application/json" -d '{"level":4}'
curl "localhost:8080/api/stats?range=daily"
```

## 프론트엔드 (Vue + Vite)

- 개발 서버 실행(프록시로 `/api`는 8080으로 전달):

```bash
cd frontend
npm install
npm run dev
```

- 프로덕션 빌드(산출물은 Spring `static/`으로 출력):

```bash
cd frontend
npm run build
```

- 페이지
  - 대시보드: `/` (Chart.js 4개 그래프, 저장 기능)
  - 히스토리: `/history` (조회/수정/삭제)
