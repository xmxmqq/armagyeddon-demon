package com.blockchain.armagyeddon;

import com.blockchain.armagyeddon.domain.Gye;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GyeService {


    public static List<Gye> getGye_all() {
        List<Gye> result = new ArrayList<>();

        HttpURLConnection con = null;
        JSONObject responseJson = null;

        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http:localhost:8080/gye";
            // URL 설정
            URL url = new URL(targetUrl);

            con = (HttpURLConnection) url.openConnection();
            // http request 형식 설정
            con.setRequestMethod("GET");

            // 보내고 결과값 받기
            int responseCode = con.getResponseCode();

            // error 출력
            if (responseCode == 400) {
                System.out.println("400:: 해당 명령을 실행할 수 없음");
            } else if (responseCode == 401) {
                System.out.println("401:: X-Auth-Token Header가 잘못됨");
            } else if (responseCode == 500) {
                System.out.println("500:: 서버 에러, 문의 필요");
            } else {

                // 성공 후 응답 JSON 데이터받기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                responseJson = new JSONObject(sb.toString());

                JSONArray arr = responseJson.getJSONArray("gye array");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject gye_Json = arr.getJSONObject(i);
                    result.add(GyeService.parseFromJson(gye_Json));

                }

//                for (JSONObject gye_Json : arr) {
//                    result.add(GyeService.parseFromJson(gye_Json));
//
//                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("not JSON Format response");
            e.printStackTrace();
        }

        return result;
    }

    public static Gye parseFromJson(JSONObject jsonObject) {
        Gye result = new Gye();
        try {
            result.setId(jsonObject.getLong("id"));
            result.setType(jsonObject.getString("type"));
            result.setInterest(jsonObject.getString("interest"));
            result.setTitle(jsonObject.getString("title"));
            result.setTargetMoney(jsonObject.getInt("target money"));
            result.setPeriod(jsonObject.getInt("period"));
            result.setTotalMember(jsonObject.getInt("total member"));
            result.setState(jsonObject.getString("state"));
            result.setMaster(jsonObject.getString("master"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
