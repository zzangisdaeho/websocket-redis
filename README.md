# 프로젝트 개요
websocket을 통한 클라이언트와 실시간 통신부.

clustering을 위해 커넥션 정보를 host정보와 같이 repository에 저장

동시에 외부 channel에 해당 서버의 host를 등록하여 해당 채널로 들어오는 요청을 기반으로 접속중인 유저 한명 혹은 전부에게 데이터를 push해준다.

## 주의사항

- 매개변수

        실행 시 command line arguments로 SERVICE.PREFIX값 꼭 줘야함.

        ex1) java -jar {app}.jar --SERVICE.PREFIX={value}
        ex2) java -SERVICE.PREFIX={value} -jar {app}.jar

        커맨드라인인수, VM옵션, OS환경변수, propertySource 뭐든 상관없다.

        다만 user, car 등 클러스터링을 source 변경 없이 올리기 위함이니 property에 넣으면 화낼지도모름

        왠만하면 도커쓰면 도커환경변수로 줍시다..

- profile

      구현체 profile 선택해야합니다.
      현재로써는 repository와 listening channel(push의 트리거가 되는 channel) 모두 redis를 사용하도록 구성되어있습니다.
      추후 db를 사용한다거나 message broker를 사용할 수 있는 가능성만 열어둔 상태입니다.
      실행시 redis profile을 추가해줘야합니다

      ex1) java -jar {app}.jar -spring.profiles.active={운영환경},redis
      ex2) java -Dspring.profiles.active={운영환경},redis -jar {app}.jar

- 필요 middle ware
  - redis (channel과 repository의 구현체가 달라지면 바뀔 수 있음)

## 빌드 및 실행
- docker build

        docker build -t websocket_redis .

- docker run

        docker run -d --name websocket_redis --network network-local -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker,redis -e SERVICE.PREFIX=USER websocket_redis:latest
        docker run -d --name websocket_redis2 --network network-local -p 8081:8080 -e SPRING_PROFILES_ACTIVE=docker,redis -e SERVICE.PREFIX=USER websocket_redis:latest
