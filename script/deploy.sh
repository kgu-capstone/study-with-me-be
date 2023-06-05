#!/bin/bash

REPOSITORY=/app/build/libs
PROJECT_NAME=StudyWithMe

# 1. Production Level에서의 Profile 설정하기
echo "> ## 1. Production Level에서의 Profile 설정하기 ##"

PROFILE="prod"

echo "> Profile = $PROFILE"

# 2-1. 현재 구동중인 Application PID 확인
echo "> ## 2. 현재 구동중인 Application PID 확인 ##"

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.jar)

echo "> 현재 구동중인 Application PID = $CURRENT_PID"

# 2-2. PID에 해당하는 Application 종료
if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다"
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

# 3. 새로운 Application 배포
echo "> ## 3. 새로운 Application 배포 ##"
JAR_NAME=$(ls -tr $REPOSITORY/*jar | tail -n 1)

echo "> JAR Name = $JAR_NAME"
echo "> $JAR_NAME 에 실행권한 부여"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -jar -Dspring.profiles.active=$PROFILE $JAR_NAME > /dev/null 2>&1 &

echo "> $JAR_NAME 실행 완료"
