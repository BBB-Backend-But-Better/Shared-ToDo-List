# CONTRIBUTING

## 브랜치 전략(전부 소문자로만 작성)
- `main`: 배포 가능한 안정 버전
- `develop`: 다음 배포를 위한 통합 브랜치 (기본 작업 브랜치)
- `feature/*`: 새로운 기능 개발 (ex: feat/users) 
- `hotfix/*`: 긴급 수정

## 작업 흐름
1. 이슈 생성 → 본인 할당
2. `develop`에서 최신 pull 받고 `feat/이슈번호-간단설명` 브랜치 생성
3. 작업 후 커밋 메시지 규칙 준수
4. PR → develop으로 보낼 때 최소 1명 리뷰 필수
(Required number of approvals: 1 (최소 1명 리뷰 필수))
5. 리뷰 반영 후 merge (Squash and Merge 권장)

## 커밋 메시지 규칙
- feat: 새로운 기능
- fix: 버그 수정
- refactor: 코드 리팩토링
- docs: 문서 수정
- test: 테스트 추가/수정
- chore: 기타 (설정 등)

예시: `[feat]: add user signup endpoint`

## 코드 스타일
- Lombok 사용
- 메서드명은 동사로 시작, 변수명은 camelCase

## PR 탬플릿 초안

📌 관련 이슈

- close #이슈번호

✨ 구현 내용
<!-- 어떤 기능을 추가/수정했는지 간단히 설명 -->

✅ 체크리스트
- [ ] 코드 컨벤션 준수
- [ ] 빌드 에러 없음
- [ ] conflict 해결 완료
- [ ] 리뷰어 지정 완료