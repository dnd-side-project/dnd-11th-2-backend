package com.dnd.runus.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PACKAGE;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
public enum WeatherType {
    CLEAR("매우 맑은 날", "러닝하기 최적의 날씨입니다! 적절한 수분섭취와 함께 달려보세요.", "https://d27big3ufowabi.cloudfront.net/weather/A1.png"),
    CLOUDY(
            "조금 흐린 날",
            "러닝하기에 좋은 환경입니다. 적당한 속도로 천천히 몸을 풀며 달려보세요.",
            "https://d27big3ufowabi.cloudfront.net/weather/A2.png"),
    CLOUDY_MORE(
            "흐린 날",
            "기온이 낮아질 수 있으니 몸을 충분히 워밍업하고 체온유지에 신경써야해요. ",
            "https://d27big3ufowabi.cloudfront.net/weather/A3.png"),
    RAIN("비내리는 날", "빗물이 고인 곳이 많을 수 있으니 달리며 미끄러지지 않도록 조심하세요", "https://d27big3ufowabi.cloudfront.net/weather/A4.png"),
    SNOW("눈 오는 날", "도로가 미끄러울 수 있으니, 러닝 시 주변 환경을 잘 살피고 조심하세요.", "https://d27big3ufowabi.cloudfront.net/weather/B1.png"),
    FOG("안개 낀 날", "시야가 제한되므로 주의가 필요합니다. 익숙한 코스를 선택하세요.", "https://d27big3ufowabi.cloudfront.net/weather/B2.png"),
    STORM("폭풍우 오는 날", "매우 위험하므로 야외 러닝은 피하고 실내에서의 대체 운동을 권장해요.", "https://d27big3ufowabi.cloudfront.net/weather/B3.png"),
    HEAT_WAVE(
            "매우 더운 날",
            "폭염주의보가 발효 중입니다.충분한 수분 섭취와, 열사병 증상이 느껴지면 즉시 러닝을 중단하세요.",
            "https://d27big3ufowabi.cloudfront.net/weather/C1.png"),
    COLD_WAVE(
            "매우 추운 날",
            "한파주의보가 발효 중입니다. 러닝 전에 충분한 워밍업으로 근육을 풀어주세요. ",
            "https://d27big3ufowabi.cloudfront.net/weather/C2.png"),
    ;
    private final String koreanName;
    private final String koreanDescription;
    private final String iconUrl;
}
