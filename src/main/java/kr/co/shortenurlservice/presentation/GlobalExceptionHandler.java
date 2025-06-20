package kr.co.shortenurlservice.presentation;

import kr.co.shortenurlservice.domain.LackOfShortenUrlKeyException;
import kr.co.shortenurlservice.domain.NotFoundShortenUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

/*
    [아래로 갈수록 더 심각하거나 중요한 로그(더 높은 레벨)]
        * TRACE - 가장 세부적인 수준의 로그로, 코드의 '세부적인' 실행 경로를 추적할 때 사용(양이 많고, 저장공간도 많이 차지함 그래서 기간을 정해서 보관하는게 좋음)
        * DEBUG - 디버깅 목적의 로그로, 개발 중 코드의 상태나 흐름을 이해하기 위해 사용 (주로 찾게될 내용은 DEBUG)
        * INFO  - 시스템의 정상적인 운영 상태를 나타내는 정보성 로그로,'중요한 이벤트나 상태 변화'를 기록 (가장 일반적인 로그, 레벨을 구분해서 찍기 어렵다면 INFO 레벨로 로그 남기자)
            ㄴ 다만 모든 로그를 인포레벨로 찍으면 로그 레벨을 제대로 활용할 수 없기 때문에 무분별하게  남기지는것은 좋은 습관이 아니다.
        * WARN - 잠재적으로 문제가 될 수 있는 상황을 나타내지만, 시스템 운영에는 즉각적으니 영향을 주지 않는 경우 사용 (경고)
        * ERROR - 치명적이지 않지만, 중요한 문제가 발생했을때 나타냅니다. 복구가 필요하거나 실패한 작업을 추적해야 할 때 사용 ( 장애 )
        * FETAL(페이탈) - 시스템 운영을 계속할 수 없을 정도로 심각한 오류가 발생했을 때 사용

    * [레벨에 따른 사용 법]
    *  - TRACE, DEBUG : 3일치 로그만 보관
    *  - WARN : 1분간 10회 이상 발생 시 알람 + 일단위 리포트
    *  - ERROR , FATAL : 1회라도 발생 시 알람!
* */

    @ExceptionHandler(LackOfShortenUrlKeyException.class)
    public ResponseEntity<String> handleLackOfShortenUrlKeyException(
            LackOfShortenUrlKeyException ex
    ) {
        // 개발자에게 알려줄 수 있는 수단 필요
        return new ResponseEntity<>("단축 URL 자원이 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundShortenUrlException.class)
    public ResponseEntity<String> handleNotFoundShortenUrlException(
            NotFoundShortenUrlException ex
    ) {
        //트러블 슈팅에 도움되기 위해서는 어떤 값으로 접근했는지 찍어주는게 좋겠지? (ex)
        log.info(ex.getMessage());//getMessage로 message 내용을 꺼내옴. (🚩개발자가 보는 로그 값)
        /*
         *       시간           |  레벨 (INFO) |   PID (10964)  |   스레드 이름 ([nio-8080-exec-1])  |  패키지 + 클래스(k.c.s.p.GlobalExceptionHandler)
         *
         * 2025-06-13T15:23:11.153+09:00  INFO 10964 --- [nio-8080-exec-1] k.c.s.p.GlobalExceptionHandler           : 단축 URL을 찾지 못했습니다. shortenUrlKey = 블라블라
         * */



        return new ResponseEntity<>("단축 URL을 찾지 못했습니다.", HttpStatus.NOT_FOUND);//404, 🚩사용자가 보는 값
    }


}
