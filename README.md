# Team16_BE
16조 백엔드

# 9주차 코드리뷰
1. 카카오 인증 관련 api 요청 보내는 로직을 KakaoAuthClient라는 클래스로 분리했는데 @Service를 붙이는게 맞는지 @Component를 붙이는게 맞는지 궁금합니다. 그리고 @Component를 붙인다면 service 패키지가 아니라 다른 패키지로 옮기는게 좋을까요? 
2. MemberService의 deleteMember 메서드와 getKakaoAccessToken 메서드를 보면 트랜잭션 내에서 외부 api를 호출하고 있는데 이 부분을 어떻게 분리하면 좋을까요? 
3. 현재 aws의 ec2의 docker-compose를 통해 배포를 하려고 Dockerfile, docker-compose.yml, nginx.conf를 작성해 추가했습니다. 이걸 깃에 추가해도 되는건지 궁금합니다!
