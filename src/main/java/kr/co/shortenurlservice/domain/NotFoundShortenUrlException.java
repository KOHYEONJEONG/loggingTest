package kr.co.shortenurlservice.domain;

public class NotFoundShortenUrlException extends RuntimeException {
    //Unchecked Exception
    //Unchecked Exception은 모두 RuntimeException 클래스를 직접 상속하거나 간접적으로 상속한 예외 클래스들이야.

    //예외 메시지를 담아줌
    public NotFoundShortenUrlException(String message){
        super(message);
    }
}
