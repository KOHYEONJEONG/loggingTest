package kr.co.shortenurlservice.presentation;

import jakarta.servlet.http.HttpSession;
import kr.co.shortenurlservice.domain.LackOfShortenUrlKeyException;
import kr.co.shortenurlservice.domain.NotFoundShortenUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler { //에러 핸들링 관리

/*
    [아래로 갈수록 더 심각하거나 중요한 로그(더 높은 레벨)]
        * TRACE - 가장 세부적인 수준의 로그로, 코드의 '세부적인' 실행 경로를 추적할 때 사용(이런 로그는 양이 많고, 저장공간도 많이 차지함 그래서 기간(ex 3일)을 정해서 보관하는게 좋음) - 쿼리 등)
        * DEBUG - 디버깅 목적의 로그로, 개발 중 코드의 상태나 흐름을 이해하기 위해 사용 (서비스 개발 과정 시 종종 의심되는 지점에 디버그 로그를 남겨 볼 수 있게~, 오랫동안 로그를 남게하고 주로 찾게될 내용은 DEBUG로 남기자 - 쿼리 등)
        * INFO  - 시스템의 정상적인 운영 상태를 나타내는 정보성 로그로,'중요한 이벤트나 상태 변화'를 기록 (쿼리X:너무 많이 남음, 비즈니스 로직 실행 결과 위주로)
            (가장 일반적인 로그, 레벨을 구분해서 찍기 어렵다면 INFO 레벨로 로그 남기자)
            ㄴ 다만 모든 로그를 인포 레벨로 찍으면 로그 레벨을 제대로 활용할 수 없기 때문에 무분별하게 남기지는것은 좋은 습관이 아니다.
            ㄴ 오류는 아니면서도, 시스템에서 중요한 기능의 실행이나 혹은 비즈니스 로직 실행 결과 같은걸 인포레벨로 찍자.
        * WARN - 경고, 당장 문제는 되지 않지만 잠재적으로 문제가 될 수 있는 상황을 나타내지만, 시스템 운영에는 즉각적으니 영향을 주지 않는 경우 사용 (일단 두고, api 응답 시간이 60초가 넘게 알람이 10번이상 오게되면 성능개선을 하겟다.)
        * ERROR - 치명적이지 않지만, 중요한 문제가 발생했을때 나타냅니다. 복구가 필요하거나 실패한 작업을 추적해야 할 때 사용 ( 로직 실행 시 에러, 장애  - 담당자가 개입해야하는 에러라면)
        * FETAL(페이탈) - 시스템 운영을 계속할 수 없을 정도로 심각한 오류가 발생했을 때 사용



    * [레벨에 따른 사용 법 - 라이브러리 있으니까 그거 쓰자]
    *  - TRACE, DEBUG : 3일치 로그만 보관
    *  - WARN : 1분간 10회 이상 발생 시 알람 + 일단위 리포트 (알람을 받을 수 있는데 WARN 레벨은 어떤 서버스를 하고 있냐에 따라서 그리고 어떤 상황이냐에 따라서 달라지기 떄문에 검토하고 사용하기)
    *  - ERROR , FATAL : 1회라도 발생 시 알람이 가도록!
* */

    @ExceptionHandler(LackOfShortenUrlKeyException.class)
    public ResponseEntity<String> handleLackOfShortenUrlKeyException(
            LackOfShortenUrlKeyException ex
    ) {
        // 개발자에게 알려줄 수 있는 수단 필요
        return new ResponseEntity<>("단축 URL 자원이 부족합니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundShortenUrlException.class)
    public ResponseEntity<String> handleNotFoundShortenUrlException(NotFoundShortenUrlException ex) {
        //트러블 슈팅에 도움되기 위해서는 어떤 값으로 접근했는지 찍어주는게 좋겠지? (ex)
        log.error(ex.getMessage());//getMessage로 message 내용을 꺼내옴. (🚩개발자가 보는 로그 값)
        //log.warn(ex.getMessage());//담당자가 개입해야하는 에러라면 warn 레벨찍기
        /*
         *       시간           |  레벨 (INFO) |   PID (10964)  |   스레드 이름 ([nio-8080-exec-1])  |  패키지 + 클래스(k.c.s.p.GlobalExceptionHandler)
         *
         * 2025-06-13T15:23:11.153+09:00  INFO 10964 --- [nio-8080-exec-1] k.c.s.p.GlobalExceptionHandler           : 단축 URL을 찾지 못했습니다. shortenUrlKey = 블라블라
         * */
        return new ResponseEntity<>("단축 URL을 찾지 못했습니다.", HttpStatus.NOT_FOUND);//404, 🚩사용자가 보는 값
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
//          이런 형식으로 요청 파라미터가 넘어온다면
//        {
//            "originalUrl": "HTTP://"
//        }
        
        StringBuilder errorMessage = new StringBuilder("유효성 검증 실패/ ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
           errorMessage.append(String.format("필드) %s / 설명) %s", error.getField(), error.getDefaultMessage()));
           //사용자 응답 BODY => 유효성 검증 실패: 필드 originalUrl : 올바른 URL이어야 합니다
        });

        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }


}
