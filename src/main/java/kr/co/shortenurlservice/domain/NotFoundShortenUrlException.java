package kr.co.shortenurlservice.domain;

public class NotFoundShortenUrlException extends RuntimeException {
    //Unchecked Exception
    //Unchecked Exception은 모두 RuntimeException 클래스를 직접 상속하거나 간접적으로 상속한 예외 클래스들이야.

    
    //기본 생성자는 안 만듬(필요없어서)
    
    //예외 메시지를 담아줌( 개발자가 트러블 슈팅에 도움되는 값을 보기 위한 message )
    public NotFoundShortenUrlException(String message){
        super(message);
    }//생성자 호출(무조건 메시지를 받는 생성자만 만들었다)
}
