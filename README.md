# 여기서 구해볼래? `Backend`

## 🌙 소개
#### 스터디 모집에서 진행 관리까지 케어하는 웹 애플리케이션 플랫폼

![1  설명](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/dede40ea-94be-4577-9674-d3e35b7cb6c0)

> 비용 문제로 인해 Naver Cloud Platform Server는 비활성화 상태

<br>

## 🖥 서비스 화면
### 스터디 찾기

![2  스터디 찾기](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/11c38c25-1dda-45ae-8239-67f5cf5894f3)

### 스터디 만들기

![3  스터디 생성](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/e19da0b7-c882-4401-9c15-06db9af517ab)

### 스터디 활동하기

![4  스터디 활동 (1)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/9b854301-c1b3-4151-abcf-0deccd7ec8e9)

![5  스터디 활동 (2)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/f44447cb-0d0a-4ec1-afe3-2f9064f5c53c)

![6  스터디 활동 (3)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/b586a8b8-6332-4077-ab5a-1f5f043fbcc9)

![7  스터디 활동 (4)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/3c8a2502-6192-4dc4-8e64-31201769998c)

### 스터디 졸업하기

![8  스터디 졸업](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/1e9527a1-d2a0-422b-90ff-d75322f1370e)


#### [API 명세서 바로가기](https://sjiwon.notion.site/API-Docs-f2c3261488a24c56bf39f7cb6da23326?pvs=4)

<br>

## 🛠 Tech Stacks
### Backend

![BE 스택](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/2c65f1ea-02b2-4782-895f-eba9b1abd2f1)

### Infra

![Infra 스택](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/cc23b8e5-efe8-4955-91cd-21a4e533c593)

<br>

## ⚙️ Infrastructure

![사용자 요청 흐름도](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/2c1c90bb-7538-45b0-bc2e-04c8f48e497c)

<br>

## 🔀 CI/CD Pipeline

![BE CI-CD](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/b8ef0c56-3d43-4767-8b9e-7bf78d6b06e7)

<br>

## 👥 팀원
|<img width="150px" src="https://avatars.githubusercontent.com/u/51479381?v=4"/>|<img width="150px" src="https://avatars.githubusercontent.com/u/109421279?v=4"/>|
|:---:|:---:|
|[서지원](https://github.com/sjiwon)|[양채린](https://github.com/chaeeerish)|

> [Frontend Repository 보러가기](https://github.com/kgu-capstone/study-with-me-fe)

<br>

## 🚩 실행 방식
### 1) MySQL DB
```docker
docker-compose up
```

- MySQL Docker Container 활성화

<br>

### 2) API Server
#### yml 설정 변수 외부 주입 (local profile)
- `NAVER_EMAIL_USERNAME` = 네이버 계정 이메일
- `NAVER_EMAIL_PASSWORD` = 네이버 계정 비밀번호
- `OAUTH_GOOGLE_CLIENT_ID` = Google OAuth Application Client Id
- `OAUTH_GOOGLE_CLIENT_SECRET` = Google OAuth Application Client Secret
- `OAUTH_GOOGLE_REDIRECT_URL` = Google OAuth Application Redirect Url
- `NCP_ACCESS_KEY` = Naver Cloud Platform Access Key
- `NCP_SECRET_KEY` = Naver Cloud Platform Secret Key
- `NCP_BUCKET_NAME` = Naver Cloud Platform Object Storage Bucket
- `SLACK_WEBHOOK_URL` = Slack Webhook Url

#### (방법-1) 빌드된 JAR 파일 실행
```shell
java -jar \
    -Dfile.encoding=UTF-8 \
    -Dspring.mail.username="네이버 계정 이메일" \
    -Dspring.mail.password="네이버 계정 비밀번호" \
    -Doauth2.google.client-id="Google OAuth Application Client Id" \
    -Doauth2.google.client-secret="Google OAuth Application Client Secret" \
    -Doauth2.google.redirect-url="Google OAuth Application Redirect Url" \
    -Dcloud.ncp.credentials.access-key="Naver Cloud Platform Access Key" \
    -Dcloud.ncp.credentials.secret-key="Naver Cloud Platform Secret Key" \
    -Dcloud.ncp.storage.bucket="Naver Cloud Platform Object Storage Bucket" \
    -Dslack.webhook.url="Slack Webhook Url" \
./build/libs/StudyWithMe.jar
```

#### (방법-2) IntelliJ 환경변수 설정 & 서버 ON
