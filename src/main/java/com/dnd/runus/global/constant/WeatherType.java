package com.dnd.runus.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PACKAGE;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
public enum WeatherType {
    CLEAR("맑음", "해가 쨍쨍하니 러닝하면 어떨까요?", "https://d27big3ufowabi.cloudfront.net/weather/A1.png"),
    CLOUDY("구름 조금", "구름이 조금 있는 날씨입니다", "https://d27big3ufowabi.cloudfront.net/weather/A2.png"),
    CLOUDY_MORE("흐림", "구름이 많은 날씨입니다", "https://d27big3ufowabi.cloudfront.net/weather/A3.png"),
    RAIN("비내리는 날", "빗물이 고인 곳이 많을 수 있으니 달리며 미끄러지지 않도록 조심하세요", "https://d27big3ufowabi.cloudfront.net/weather/A4.png"),
    SNOW("눈", "눈길에 미끄러지지 않도록 조심하세요", "https://d27big3ufowabi.cloudfront.net/weather/B1.png"),
    FOG("안개", "안개가 짙으니 시야확보에 유의하세요", "https://d27big3ufowabi.cloudfront.net/weather/B2.png"),
    STORM("폭풍", "폭풍이 몰아치는 날씨입니다. 외부 활동을 삼가하세요", "https://d27big3ufowabi.cloudfront.net/weather/B3.png"),
    HEAT_WAVE("폭염", "기온이 매우 높으니 충분한 수분 섭취에 유의하세요", "https://d27big3ufowabi.cloudfront.net/weather/C1.png"),
    COLD_WAVE("한파", "기온이 매우 낮으니 러닝 전 따뜻하게 준비하세요", "https://d27big3ufowabi.cloudfront.net/weather/C2.png"),
    ;
    private final String koreanName;
    private final String koreanDescription;
    private final String iconUrl;
}
