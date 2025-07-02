package kr.co.shortenurlservice.infrastructure;

import kr.co.shortenurlservice.domain.ShortenUrl;
import kr.co.shortenurlservice.domain.ShortenUrlRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MapShortenUrlRepository implements ShortenUrlRepository {

    private Map<String, ShortenUrl> shortenUrls = new ConcurrentHashMap<>();//(동기화 : 스레드 안정성)멀티스레드, 안전하게 동시에 접근할 수 있게 만들어진 컬렉션이다.
    //HashMap : 단일 스레드 환경
    //synchronized  :복합로직이라면 같이 써주자.
    //    synchronized (map) {
    //        if (!map.containsKey("a")) {
    //            map.put("a", "apple");
    //        }
    //    }
    @Override
    public void saveShortenUrl(ShortenUrl shortenUrl) {
        shortenUrls.put(shortenUrl.getShortenUrlKey(), shortenUrl);
    }

    @Override
    public ShortenUrl findShortenUrlByShortenUrlKey(String shortenUrlKey) {
        ShortenUrl shortenUrl = shortenUrls.get(shortenUrlKey);
        return shortenUrl;
    }

    @Override
    public List<ShortenUrl> findAll() {
        return new ArrayList<>(shortenUrls.values());
    }

}
