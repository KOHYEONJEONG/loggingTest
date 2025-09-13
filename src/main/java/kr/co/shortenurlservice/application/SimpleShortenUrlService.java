package kr.co.shortenurlservice.application;

import kr.co.shortenurlservice.domain.LackOfShortenUrlKeyException;
import kr.co.shortenurlservice.domain.NotFoundShortenUrlException;
import kr.co.shortenurlservice.domain.ShortenUrl;
import kr.co.shortenurlservice.domain.ShortenUrlRepository;
import kr.co.shortenurlservice.presentation.ShortenUrlCreateRequestDto;
import kr.co.shortenurlservice.presentation.ShortenUrlCreateResponseDto;
import kr.co.shortenurlservice.presentation.ShortenUrlInformationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* 컨트롤러에 들어오는 요청들에 로그는 필터에서 찍고
* 서비스 안에있는 메서드는 AOP에서 로그찍는가봐
* */

@Slf4j
@Service
public class SimpleShortenUrlService {

    private ShortenUrlRepository shortenUrlRepository;

    @Autowired
    SimpleShortenUrlService(ShortenUrlRepository shortenUrlRepository) {
        this.shortenUrlRepository = shortenUrlRepository;
    }

    //단축 url 생성(비즈니스 로직 중요한 부분)
    public ShortenUrlCreateResponseDto generateShortenUrl(ShortenUrlCreateRequestDto shortenUrlCreateRequestDto) {

        //로그레벨 구분하여 찍기
        log.info("generateShortenUrl {}", shortenUrlCreateRequestDto.getOriginalUrl());
        String originalUrl = shortenUrlCreateRequestDto.getOriginalUrl();
        String shortenUrlKey = getUniqueShortenUrlKey();

        ShortenUrl shortenUrl = new ShortenUrl(originalUrl, shortenUrlKey);
        shortenUrlRepository.saveShortenUrl(shortenUrl);
        log.info("shortenUrl 생성 :{}", shortenUrl);//originalUrl, shortenUrlKey 을 한번에 출력

        ShortenUrlCreateResponseDto shortenUrlCreateResponseDto = new ShortenUrlCreateResponseDto(shortenUrl);
        return shortenUrlCreateResponseDto;
    }

    public String getOriginalUrlByShortenUrlKey(String shortenUrlKey) {
        ShortenUrl shortenUrl = shortenUrlRepository.findShortenUrlByShortenUrlKey(shortenUrlKey);

        if(null == shortenUrl)
            throw new NotFoundShortenUrlException("단축 URL을 찾지 못했습니다. shortenUrlKey = "+shortenUrlKey);

        // 앞뒤로 redirectCount가 증가되기 전/후 찍어보는것도 좋다.(동시성 문제가 발생되는 부분을 디버그 로그를 찍어 의시되는 부분을 줄여 나가자)
        shortenUrl.increaseRedirectCount();

        shortenUrlRepository.saveShortenUrl(shortenUrl);

        String originalUrl = shortenUrl.getOriginalUrl();

        return originalUrl;
    }

    public ShortenUrlInformationDto getShortenUrlInformationByShortenUrlKey(String shortenUrlKey) {
        ShortenUrl shortenUrl = shortenUrlRepository.findShortenUrlByShortenUrlKey(shortenUrlKey);

        if(null == shortenUrl)
            throw new NotFoundShortenUrlException("단축 URL을 찾지 못했습니다. shortenUrlKey = "+shortenUrlKey);

        ShortenUrlInformationDto shortenUrlInformationDto = new ShortenUrlInformationDto(shortenUrl);

        return shortenUrlInformationDto;
    }

    public List<ShortenUrlInformationDto> getAllShortenUrlInformationDto() {
        List<ShortenUrl> shortenUrls = shortenUrlRepository.findAll();

        return shortenUrls
                .stream()
                .map(shortenUrl -> new ShortenUrlInformationDto(shortenUrl))
                .toList();
    }

    private String getUniqueShortenUrlKey() {
        final int MAX_RETRY_COUNT = 5;
        int count = 0;

        while(count++ < MAX_RETRY_COUNT) {
            String shortenUrlKey = ShortenUrl.generateShortenUrlKey();
            ShortenUrl shortenUrl = shortenUrlRepository.findShortenUrlByShortenUrlKey(shortenUrlKey);

            if(null == shortenUrl)
                return shortenUrlKey;
        }

        // 재시도를 하게 되는 곳
        log.warn("단축 URL 생성 재시도! 재시도 횟수 : {}", count+1);


        throw new LackOfShortenUrlKeyException();
    }

}
