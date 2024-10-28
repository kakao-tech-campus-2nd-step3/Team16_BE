# Team16_BE
16조 백엔드

# 7,8주차 코드리뷰
1. 프론트 요청이랑 관계 없이 톡캘린더 api 요청 날릴 땐 한 달 단위로 가져다 놓고 거기서 프론트에서 요청한 부분만 골라서 사용하는 것에 대해 어떻게 생각하시는지 궁금합니다.
2. 어노테이션을 사용하면 편리하고 redisTemplate로 작성하면 자세하게 사용 가능합니다. 일정 조회할 때 memberId, from, to를 사용하여 캐시에 저장하고 일정 생성할 때도 똑같이 memberId, from, to를 사용하여 캐시를 삭제해야 하는데, 일정을 삭제하는 경우에는 from과 to가 불필요하다 생각했습니다.    
어노테이션에서는 와일드문자인 '*'을 사용할 수 없어 memberId 뒤에 을 붙이지 못해 일정 생성 때도 from과 to가 있어야 일정 조회 때 사용하는 캐시를 삭제할 수 있습니다. 하지만 redisTemplate를 사용하면 와일드 문자인 ''이 사용 가능하여 memberId 뒤에 from과 to가 없어도 memberId 별로 삭제가 가능합니다. 그래서 redisTemplate로 변경해서 사용해는데 괜찮은지 궁금합니다!
3. RedisConfig에서 RedisTemplate<String, Object>로 사용했는데
   TalkCalendarService의 getUpdatedEventList 메서드의 List<EventGetResponse> cachedEvents = (List<EventGetResponse>) redisTemplate.opsForValue()
   .get(cacheKey); 부분에서
   Unchecked cast: 'java.lang.Object' to 'java.util.List<org.cookieandkakao.babting.domain.calendar.dto.response.EventGetResponse>'. Reason: 'redisTemplate.opsForValue()' has raw type, so result of get is erased 이러한 경고창이 나옵니다. raw type을 사용하고 있어서 경고가 나온다는 것 같아 RedisTemplate의 Object 부분에 dto를 넣었더니 에러가 발생했습니다. 이 부분을 어떻게 해결하면 좋을지 궁금합니다!
