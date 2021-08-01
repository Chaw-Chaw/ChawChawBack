package com.project.chawchaw.service;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.chawchaw.dto.social.FaceBookProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@Service
public class FaceBookService {

    private final RestTemplate restTemplate;
    private final Environment env;
    private final Gson gson;


    public FaceBookProfile getFaceBookProfile(String accessToken, String userId) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("fields", "id,name,email");
        params.add("access_token", accessToken);



        UriComponents uri = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/"+userId).
                queryParam("fields", "id,name,email,picture").
                queryParam("access_token", accessToken).build();

        // Set http entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params);
        String response = restTemplate.getForObject(
                uri.toUri(),

                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        FaceBookProfile profile  =new FaceBookProfile();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            profile.setEmail(String.valueOf(userId));
            String name = String.valueOf(jsonNode.get("name"));
            profile.setName(name.substring(1, name.length() - 1));
            String imageUrl = String.valueOf(jsonNode.get("picture").get("data").get("url"));
            profile.setImageUrl(imageUrl.substring(1, imageUrl.length() - 1));
            profile.setProvider("facebook");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return profile;

    }
}