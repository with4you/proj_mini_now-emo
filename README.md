<div align="center">

# emotion-snapshot

감정 로그를 기록하고 통계를 시각화하는 미니 앱 (Spring Boot + Vue).

</div>

---

## ✨ 특징
- CRUD: 감정 레벨(1..5) 생성/조회/수정/삭제
- 통계: 오늘(시간별) / 이번주(요일별) / 이번달(일별) / 올해(월별)
- UI: Vue 3 + Vite + Chart.js (다크 테마, 세련된 차트 스타일)
- 데이터 시드: CSV(예: `level,created_at`)를 부팅 시 자동 로드

## 🧱 스택
- Backend: Java 17, Spring Boot 3.x, Spring Web, Spring Data JPA, H2, Validation, (Actuator: health)
- Frontend: Vue 3, Vite, Chart.js

## 📸 스크린샷

<img width="587" height="961" alt="dashboard" src="https://github.com/user-attachments/assets/d9d356a2-7839-4ed3-a8a1-24778974b2aa" />
<img width="593" height="954" alt="dashboard2" src="https://github.com/user-attachments/assets/f77544b5-c937-4953-a7c4-9d3e0d5a650e" />
<img width="593" height="960" alt="history" src="https://github.com/user-attachments/assets/ddada56f-e8c3-4760-bd5e-56f5ea2b06eb" />

## 🚀 실행 방법

### Backend (Spring Boot)

- (로컬 Maven 사용)

```bash
mvn spring-boot:run
```

웹:
- 앱: http://localhost:8080/
- H2 콘솔: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:emotiondb`)

### Frontend (Vue + Vite)
개발용(프록시로 `/api` → 8080):

```bash
cd frontend
npm install
npm run dev
# http://localhost:5173
```

프로덕션 빌드(산출물은 Spring `static/`으로 출력):

```bash
cd frontend
npm run build
# http://localhost:8080/ 에서 Spring이 서빙
```

## 🗂️ 데이터 시드(CSV)
- 경로: `src/main/resources/data/emotion_log_2024_2025_hourly.csv`
- 포맷: `level,created_at`
  - 예: `3,2025-10-15 00:22:15` 또는 `3,2025-10-15T00:22:15Z`
- 애플리케이션 부팅 시 테이블 초기화 후 CSV를 자동 로드합니다.

## 🔌 API 요약

- POST `/api/emotions` {"level":1..5}
- GET `/api/emotions?from&to` (ISO8601 Instant)
- PATCH `/api/emotions/{id}` {"level":1..5}
- DELETE `/api/emotions/{id}`
- GET `/api/stats?range=daily|weekly|monthly|yearly`
- (옵션) GET `/actuator/health`

통계 그룹 기준
- daily: 오늘 00~23시(시간대)
- weekly: 이번 주 월~일(요일)
- monthly: 이번 달 1~말일(일)
- yearly: 올해 1~12월(월)

## 🌐 환경/설정

- JDBC 타임존: Asia/Seoul (UTC 저장/조회 일관성)
- H2 메모리 DB(dev용), 콘솔 활성화

## 🧪 빠른 테스트

```bash
curl -X POST localhost:8080/api/emotions -H "Content-Type: application/json" -d '{"level":4}'
curl "localhost:8080/api/stats?range=daily"
```

## 📦 배포/버전
- 빌드: `mvn -U clean package`
- 프런트 빌드: `cd frontend && npm run build`

## 📜 라이선스
본 레포지토리는 사내/개인 프로젝트 템플릿 용도로 사용할 수 있습니다.
