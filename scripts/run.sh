#!/bin/bash
PROJECT_NAME=dikkak-0.0.1
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

if [ -z "$CURRENT_PID" ]; then
        echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
        echo "> kill -9 $CURRENT_PID"
        sudo kill -9 $CURRENT_PID
        sleep 5
fi


echo "> 새 애플리케이션 배포"
nohup java -jar /home/ec2-user/dikkak-deploy/build/libs/dikkak-0.0.1-SNAPSHOT.jar > /home/ec2-user/nohup.out 2>&1 &

exit 0