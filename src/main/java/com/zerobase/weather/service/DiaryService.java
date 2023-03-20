package com.zerobase.weather.service;

import com.zerobase.weather.domain.DateWeather;
import com.zerobase.weather.domain.Diary;
import com.zerobase.weather.repository.DateWeatherRepository;
import com.zerobase.weather.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PropertySource("classpath:api.properties")
@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    // 1) open weather map 에서 데이터 받아오기
    // 2) 받아온 날씨 json 파싱
    // 3) 파싱된 json 데이터 + 일기 값 DB에 저장
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherString();
        Map<String, Object> parseWeather = parseWeather(weatherData);
        return DateWeather.builder()
                .date(LocalDate.now())
                .weather(parseWeather.get("main").toString())
                .icon(parseWeather.get("icon").toString())
                .temperature((Double) parseWeather.get("temp"))
                .build();

    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findALlByDate(date);
        if (dateWeatherListFromDB.size() == 0) {
            // api에서 날씨 정보를 가져와야 한다.
            // 정책에 맞게 구현 - 날씨 없이 일기 쓰기 or 현재 날씨를 가져오기 등등
            return getWeatherFromApi();
        }

        return dateWeatherListFromDB.get(0);
    }

    @Transactional
    public void createDiary(LocalDate date, String text) {
        log.info("started to create diary");
        // 1) open-weather-map 에서 weather json 데이터 가져오기
        // String weatherData = getWeatherString();
        // 2) 받아온 json 데이터를 요구사항에 맞게 파싱
        // Map<String, Object> parseWeather = parseWeather(weatherData);

        // 날씨 데이터 가져오기(API로 가져오기 or DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        // 3) 파싱된 데이터와 일기 정보를 DB에 저장
        Diary diary = Diary.builder()
                .text(text)
                .build();
        diary.setDateWeather(dateWeather);

        diaryRepository.save(diary);
        log.info("end to create diary");
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        log.debug("read diary");
//        if(date.isAfter(LocalDate.ofYearDay(3050, 1))) {
//            throw new InvalidDate();
//        }
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    // 변경하려는 날짜의 일기 중 첫번째 일기를 수정한다고 가정
    @Transactional
    public void updateDiary(LocalDate date, String text) {

        Diary firstDiary = diaryRepository.getFirstByDate(date);

        // firstDiary.setText(text);
        // diaryRepository.save(firstDiary);
        firstDiary.updateText(text);

    }

    // 또는 DiaryRepository deleteAllByDate 메서드 위에 @Transactional 을 붙여도 된다.
    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    // private method
    private Map<String, Object> parseWeather(String jsonString) {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject weather = (JSONObject) ((JSONArray) jsonObject.get("weather")).get(0);
        JSONObject main = (JSONObject) jsonObject.get("main");
        resultMap.put("main", weather.get("main"));
        resultMap.put("icon", weather.get("icon"));
        resultMap.put("temp", main.get("temp"));
        return resultMap;
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }


}
