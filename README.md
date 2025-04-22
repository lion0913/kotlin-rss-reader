# kotlin-rss-reader


## 패키지 구조
```bash
└── src
    ├── main
    │   └── kotlin
    │       ├── rssreader
    │       │   ├── controller # 컨트롤러
    │       │   ├── model
    │       │   ├── utils
    │       │   └── view # Input, Output에 대한 어쩌구
```

## 기능 구현 목록
1. 모델 정의
- [ ] rss class 선언
- [ ] Post class
2. Parser 구현
- [ ] xml to class
- [ ] 어쩌구
3. 예외처리
4. 테스트

## 3번째 작업
1. 전역 map 선언 ("검색어", "해당 검색어로 조회한 리스트의 가장 최근 글(=item)")
2. global scope 10분 주기로 map에 있는 내용 호출
3. 검색어 별 비교
   1. 추가되면 [new] 보여주고 map의 value 값 변경
   2. 추가되지 않으면 기존 리스트 리턴

## 블로그 파싱 작업중 에러가 발생했을 때 프로그램이 종료되지 않으려면?
