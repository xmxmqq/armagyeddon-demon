package com.blockchain.armagyeddon;

import com.blockchain.armagyeddon.domain.Gye;
import com.blockchain.armagyeddon.domain.Member;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GyeService {

    static String JWT;

    public static String getJWT() {

        String result = "";
        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http://localhost:8080/authenticate";

            // URL 설정
            URL url = new URL(targetUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            JSONObject userInfo = new JSONObject();

            userInfo.put("email", "admin@naver.com");
            userInfo.put("password", "password");


            // http request 형식 설정
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(userInfo.toString().getBytes("UTF-8"));
            os.flush();

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

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject jwt_json = new JSONObject(sb.toString());
                result = jwt_json.getString("token");


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JWT = result;
        return result;

    }

    public static List<Gye> getAllGye() {

        List<Gye> result = new ArrayList<>();

        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http://localhost:8080/gye";
            // URL 설정
            URL url = new URL(targetUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // http request 형식 설정
            con.setRequestMethod("GET");
            con.addRequestProperty("Authorization", "Bearer " + JWT);

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

                //Object obj = JSONValue.parse(sb.toString());
                JSONArray arr = new JSONArray(sb.toString());

                //JSONObject responseJson = new JSONArray(sb.toString());//new JSONObject(sb.toString());

                //JSONArray arr = responseJson.getJSONArray("data array");

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

    public static double getBalanceOf(String email) {
//        public static double getBalanceOf (String email){

        double result = 0;
//            String result = "";

        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http://localhost:8080/user-token/" + email;
            // URL 설정
            URL url = new URL(targetUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // http request 형식 설정
            con.setRequestMethod("GET");
            con.addRequestProperty("Authorization", "Bearer " + JWT);

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

                result = Double.parseDouble(sb.toString());

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;

    }

    // 수금
    public static String collectToken(String email, Long gyeId, String amount) {
        String result = "";
        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http://localhost:8080/user-token";

            // URL 설정
            URL url = new URL(targetUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // http request 형식 설정
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Bearer " + JWT);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            JSONObject val = new JSONObject();
            val.put("gyeId", gyeId.toString());
            val.put("email", email);
            val.put("amount", amount);

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(val.toString().getBytes("UTF-8"));
            os.flush();


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

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                result = sb.toString();


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }

    // 송금
    public static String sendToken(Long gyeId, String email, String amount) {
        String result = "";
        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http://localhost:8080/gye-token";

            // URL 설정
            URL url = new URL(targetUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // http request 형식 설정
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Bearer " + JWT);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            JSONObject val = new JSONObject();
            val.put("gyeId", gyeId.toString());
            val.put("email", email);
            val.put("amount", amount);

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(val.toString().getBytes("UTF-8"));
            os.flush();


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

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                result = sb.toString();


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }

    // 계 상태 변경
    public static String updateGye(Long gyeId, String state, String payDay) {

        String result = "";

        try {
            // BE url을 String으로 받아와서
            String targetUrl = "http://localhost:8080/gye-state/" + Long.toString(gyeId);
            // URL 설정
            URL url = new URL(targetUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // http request 형식 설정
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + JWT);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");


            JSONObject val = new JSONObject();
            val.put("gyeId", gyeId.toString());
            val.put("state", state);
            val.put("payDay", payDay.toString());

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(val.toString().getBytes("UTF-8"));
            os.flush();

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

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                result = sb.toString();


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
            result.setTargetMoney(jsonObject.getInt("targetMoney"));
            result.setPeriod(jsonObject.getInt("period"));
            result.setTotalMember(jsonObject.getInt("totalMember"));
            result.setState(jsonObject.getString("state"));
            result.setMaster(jsonObject.getString("master"));

            List<Member> members = new ArrayList();
            for (int i = 0; i < jsonObject.getJSONArray("members").length(); i++) {
                JSONObject json_mem = jsonObject.getJSONArray("members").getJSONObject(i);
                Member mem = new Member();
                mem.setEmail(json_mem.getString("email"));
                mem.setName(json_mem.getString("name"));
                mem.setTurn(json_mem.getInt("turn"));
                members.add(mem);
            }
            result.setMembers(members);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


}
