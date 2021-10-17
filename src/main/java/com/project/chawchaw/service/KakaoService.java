package com.project.chawchaw.service;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.chawchaw.dto.social.KakaoProfile;
import com.project.chawchaw.dto.social.RetKakaoAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@Service
public class KakaoService {

    private final RestTemplate restTemplate;
    private final Environment env;
    private final Gson gson;


    @Value("${spring.url.base}")
    private String baseUrl;

    @Value("${spring.social.kakao.client_id}")
    private String kakaoClientId;

    @Value("${spring.social.kakao.redirect}")
    private String kakaoRedirect;

//    public KakaoProfile getKakaoProfile1(String accessToken) {
//        System.out.println("=============================================");
//        System.out.println(accessToken);
//        // Set header : Content-type: application/x-www-form-urlencoded
//        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Bearer " +accessToken);
//        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
////        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//
//        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//
//
//        // Set http entity
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>( params,headers);
//
////        try {
////            // Request profile
////            ResponseEntity<String> response = restTemplate.postForEntity(env.getProperty("spring.social.kakao.url.profile"), request, String.class);
////            if (response.getStatusCode() == HttpStatus.OK)
////                return gson.fromJson(response.getBody(), KakaoProfile.class);
////        } catch (Exception e) {
////            throw new CommunicationException();
////        }
////        throw new CommunicationException();
//
//        UriComponents uri = UriComponentsBuilder.fromHttpUrl("https://kapi.kakao.com/v2/user/me").build();
//        ResponseEntity<String> response = restTemplate.postForEntity(uri.toUri(),request,String.class);
//
//
//      ObjectMapper objectMapper = new ObjectMapper();
//        KakaoProfile profile  =null;
//
//        try {
//            profile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
//
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        return profile;
//    }


    public KakaoProfile getKakaoProfile(String code) throws Exception {


        String accessToken = null;
        try {
            RetKakaoAuth kakaoTokenInfo = getKakaoTokenInfo(code);
            if (kakaoTokenInfo != null) {
                accessToken = kakaoTokenInfo.getAccess_token();
            } else {
                throw new Exception();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            String url = "https://kapi.kakao.com/v2/user/me";

            String response = restTemplate.postForObject(url, request, String.class);


            KakaoProfile kakaoProfile=new KakaoProfile();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode=objectMapper.readTree(response);
            kakaoProfile.setEmail(String.valueOf(jsonNode.get("id")));
            String name = String.valueOf(jsonNode.get("properties").get("nickname"));
            kakaoProfile.setName(name.substring(1,name.length()-1));
            kakaoProfile.setProvider("kakao");
            String imageUrl = String.valueOf(jsonNode.get("kakao_account").get("profile").get("profile_image_url"));
            kakaoProfile.setImageUrl(imageUrl.substring(1,imageUrl.length()-1));
            return kakaoProfile;

        } catch (HttpClientErrorException e) {
            throw new Exception();
        }
    }


    public RetKakaoAuth getKakaoTokenInfo(String code) {


        // Set header : Content-type: application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Set parameter
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", baseUrl + kakaoRedirect);
        params.add("code", code);
        // Set http entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(env.getProperty("spring.social.kakao.url.token"), request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return gson.fromJson(response.getBody(), RetKakaoAuth.class);
        }
        return null;
    }

//    public String logoutKakao(String accessToken){
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " +accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded");
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>( headers);
//
//
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://kapi.kakao.com/v1/user/logout",
//                HttpMethod.POST,
//                request,
//                String.class
//        );
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String id=null;
//        try {
//            id = objectMapper.readValue(response.getBody(), String.class);
//
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        return id;
//
//
//
//    }
}