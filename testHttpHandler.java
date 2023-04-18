import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class testHttpHandler implements HttpHandler {
    private static final String fileLocation = "src/TestNGWithSelenium_2022-05-20_2022-05-20-12-55-45.json";
    // reachable = true表示使用的是API可达性分析 否则使用组件级风险分析
    private static final boolean reachable = false;

    @Override
    public void handle(HttpExchange exchange) {
//        Process process;
//        try {
////            String[] args = new String[]{"python", "src\\parse.py", fileLocation};
////            process = Runtime.getRuntime().exec(args);
////            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////            String line = null;
//            String methodType = exchange.getRequestMethod();
//            if (methodType.equalsIgnoreCase("POST")) {
//                System.out.println("POST Request!");
//                Gson gson = new Gson();
//                JsonParser jsonParser = new JsonParser();
//                JsonObject jsonObject = (JsonObject) jsonParser.parse(new BufferedReader(new InputStreamReader(exchange.getRequestBody())));
//                Map map=gson.fromJson(jsonObject,Map.class);
//                String resp = (String) map.get("projectName");
//                System.out.println(resp);
//                Headers headers = exchange.getResponseHeaders();
//                headers.set("Content-Type", "application/json;charset=utf-8");
//                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, resp.getBytes().length);
//                exchange.getResponseBody().write(resp.getBytes());
////                InputStream requestBody = exchange.getRequestBody();
////                String params = "";
////                params = new BufferedReader(new InputStreamReader(requestBody)).readLine();
////                String[] paramsArray = params.split("&");
////                HashMap<String, String> hashMap = new HashMap<>();
////                for (String param : paramsArray) {
////                    hashMap.put(param.split("=")[0], param.split("=")[1]);
////                }
////                String username = hashMap.get("username");
////                String repoName = hashMap.get("repoName");
////                String accessToken = hashMap.get("accessToken");
////                String analyseToken = hashMap.get("analyseToken");
////                Properties properties = new Properties();
////                properties.load(new BufferedReader(new FileReader("src/config.properties")));
////                String rootDir = properties.getProperty("rootDir");
////                String repoDir = rootDir + File.separator + username + File.separator + repoName + File.separator;
////                String url = String.format("https://api.github.com/repos/%s/%s/contents", username, repoName);
////                String response = HttpRequest.get(url)
////                        .header("Content-Type", "application/json")
////                        .header("Accept", "application/json")
////                        .header("Authorization", "token " + accessToken)
////                        .execute().body();
////                try {
////                    JSONArray array = JSONUtil.parseArray(response);
////                    for (int i = 0; i < array.size(); i++) {
////                        JSONObject jsonObject = array.getJSONObject(i);
////                        if (jsonObject.getStr("type").equals("file")) {
////                            // 说明是文件
////                            resolveFetchFile(repoDir, jsonObject, accessToken);
////                        } else {
////                            // 否则是文件夹
////                            resolveFetchDir(repoDir, jsonObject, accessToken);
////                        }
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//            }
////            line = bufferedReader.readLine();
////            System.out.println(line);
////            bufferedReader.close();
////            process.waitFor();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            exchange.close();
//        }
        try {
            String methodType = exchange.getRequestMethod();
            if (methodType.equalsIgnoreCase("POST")) {
                System.out.println("POST request!");
                Properties properties = new Properties();
                properties.load(new BufferedReader(new FileReader("/home/hadoop/dfs/data/Workspace/Luchenhao/httpServer/src/config.properties")));
                String jarDir = properties.getProperty("jarDir");
                String outputPath = properties.getProperty("outputPath");
                String requestName = properties.getProperty("request");
                String propertiesPath = properties.getProperty("propertiesPath");
                String effortPath = properties.getProperty("effortPath");
                String vulDBPath = properties.getProperty("vulDBPath");
                InputStream requestBody = exchange.getRequestBody();
                String params = "";
                params = new BufferedReader(new InputStreamReader(requestBody)).readLine();
                System.out.println("Request Body: " + params);
                String[] paramsArray = params.split("&");
                HashMap<String, String> hashMap = new HashMap<>();
                for (String param : paramsArray) {
                    hashMap.put(param.split("=")[0], param.split("=")[1]);
                    System.out.println(param.split("=")[0] + " " + param.split("=")[1]);
                }
                // return 200 immediately
                String resp = "{Hello!}";
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, resp.getBytes().length);
                exchange.getResponseBody().write(resp.getBytes());
                exchange.getResponseBody().close();
                exchange.close();
                // repo clone
                String username = hashMap.get("username");
                String repoName = hashMap.get("repoName");
                String accessToken = hashMap.get("accessToken");
                String analyseToken = hashMap.get("analyseToken");
                String rootDir = properties.getProperty("rootDir");
                String authorization = hashMap.get("Authorization");
                StringBuilder sb = new StringBuilder();
                sb.append(authorization, 0, 6);
                sb.append(" ");
                sb.append(authorization.substring(7));
                authorization = sb.toString();
                System.out.println(authorization);
                try {
                    String repoDir = rootDir + File.separator + username + File.separator + repoName + File.separator;
//                String url = String.format("https://api.github.com/repos/%s/%s/contents", username, repoName);
//                HttpURLConnection httpURLConnection;
//                String response = null;
//                try {
//                    URL url1 = new URL(url);
//                    httpURLConnection = (HttpURLConnection) url1.openConnection();
//                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
//                    httpURLConnection.setRequestProperty("Accept", "application/json");
//                    httpURLConnection.setRequestProperty("Authorization", "token " + accessToken);
//                    httpURLConnection.setDoInput(true);
//                    response = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).readLine();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    JSONArray array = JSONUtil.parseArray(response);
////                    System.out.println(array.size());
//                    for (int i = 0; i < array.size(); i++) {
//                        JSONObject jsonObject = array.getJSONObject(i);
//                        if (jsonObject.getStr("type").equals("file")) {
//                            // file
//                            resolveFetchFile(repoDir, jsonObject, accessToken);
//                        } else {
//                            // folder
//                            resolveFetchDir(repoDir, jsonObject, accessToken);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                    // git clone and mvn clean install

//                System.out.println("download finished!");
                    // mvn clean install to improve the success rate of mvn:dependency:tree
                    Process processMaven;
                    try {
                        String[] args = new String[]{"python", "mvninstall.py", repoDir, accessToken};
                        processMaven = Runtime.getRuntime().exec(args);
                        System.out.println("download repo: " + repoDir + "!");
                        System.out.println("mvn install for repo: " + repoDir + "!");
                        processMaven.waitFor();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // analysis
                    if (reachable) {
                        Process process;
                        String effortFileDir = null;
                        boolean flag = false;
                        try {
                            String[] args = new String[]{"java", "-jar", jarDir, outputPath, propertiesPath, repoDir};
                            process = Runtime.getRuntime().exec(args);
                            System.out.println("analysis begin!");
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line = null;
                            while ((line = bufferedReader.readLine()) != null) {
                                System.out.println(line);
                                if (line.contains("Effort result saved:")) {
                                    flag = true;
//                            String[] temp=line.split(" ");
//                            int tempLength=temp.length;
//                            effortFileDir=temp[tempLength-1];
                                }
                            }
                            bufferedReader.close();
                            process.waitFor();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (flag) {
                            File[] files = new File(effortPath).listFiles();
                            long time = files[0].lastModified();
                            effortFileDir = files[0].getAbsolutePath();
                            for (File file : files) {
                                if (file.lastModified() > time) {
                                    time = file.lastModified();
                                    effortFileDir = file.getAbsolutePath();
                                }
                            }
                        }
                        System.out.println(effortFileDir);
                        HttpURLConnection con;
                        // repo doesn't have pom.xml or build.gradle
                        if (effortFileDir == null) {
                            try {
                                URL responseURL = new URL(requestName);
                                con = (HttpURLConnection) responseURL.openConnection();
                                con.setRequestMethod("POST");
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("message", "Unsupported type!");
                                jsonObject.addProperty("analyseToken", analyseToken);
                                con.setDoOutput(true);
                                con.setRequestProperty("Accept", "*/*");
                                con.setRequestProperty("Content-Type", "application/json");
                                String sendMessage = jsonObject.toString();
                                OutputStream os = con.getOutputStream();
                                os.write(sendMessage.getBytes());
                                con.getResponseCode();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // else case
                            try {
                                // formatting
                                String[] args = new String[]{"python", "parse.py", effortFileDir, analyseToken};
                                process = Runtime.getRuntime().exec(args);
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String line = null;
                                line = bufferedReader.readLine();
                                bufferedReader.close();
                                process.waitFor();
//                        JsonParser jsonParser=new JsonParser();
//                        JsonObject jsonObject=(JsonObject) jsonParser.parse(new FileReader(line));
                                URL responseURL = new URL(requestName);
                                con = (HttpURLConnection) responseURL.openConnection();
//                        con.setConnectTimeout(10*60*1000);
                                con.setRequestMethod("POST");
//                        JsonObject jsonObject=new JsonObject();
//                        jsonObject.addProperty("file_location",effortFileDir);
//                        jsonObject.addProperty("analyseToken",analyseToken);
                                con.setDoOutput(true);
                                con.setRequestProperty("Accept", "*/*");
                                con.setRequestProperty("Content-Type", "application/json");
//                        String sendMessage=jsonObject.toString();
//                        String sendMessage=jsonObject.toString();
                                OutputStream os = con.getOutputStream();
//                        os.write(sendMessage.getBytes());
                                BufferedInputStream bin = new BufferedInputStream(Files.newInputStream(Paths.get(line)));
//                        JsonParser jsonParser = new JsonParser();
//                        JsonObject jsonObject=(JsonObject)jsonParser.parse(new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(line)))));
                                byte[] buffer = new byte[10240];
                                int length = 0;
                                while ((length = bin.read(buffer)) != -1) {
//                            os.write(jsonObject.toString().getBytes());
//                            System.out.println(length);
                                    os.write(buffer, 0, length);
                                }
//                        os.write(sendMessage.getBytes());
                                con.getResponseCode();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Process process;
                        String effortFileDir = null;
                        boolean flag = false;
                        List<String> errorMessage = new ArrayList<>();
                        String dependencyFileLocation = null;
                        try {
                            String[] args = new String[]{"java", "-jar", jarDir, outputPath, propertiesPath, repoDir};
                            process = Runtime.getRuntime().exec(args);
                            System.out.println("analysis begin!");
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line = null;
                            while ((line = bufferedReader.readLine()) != null) {
                                System.out.println(line);
                                if (line.contains("ERROR")) {
                                    flag = true;
                                    errorMessage.add(line);
                                } else if (!flag && line.contains("filtered dependency path: ")) {
                                    dependencyFileLocation = line.split("filtered dependency path: ")[1];
                                }
                            }
                            bufferedReader.close();
                            process.waitFor();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String errorMessageSummary = "";
                        if (errorMessage.size() > 0) {
                            errorMessageSummary = errorMessage.get(0);
                            int indexStart = errorMessageSummary.indexOf("Failed ");
                            int indexEnd = errorMessageSummary.indexOf("Failure ");
                            int indexEnd1 = errorMessageSummary.indexOf("Cannot ");
                            if (indexEnd != -1) {
                                errorMessageSummary = errorMessageSummary.substring(indexStart, indexEnd - 2);
                            } else {
                                errorMessageSummary = errorMessageSummary.substring(indexStart, indexEnd1 - 2);
                            }
//                        errorMessageSummary="依赖解析失败，\""+errorMessageSummary+"\"暂不支持该项目的检测";
                            System.out.println("Error message: " + errorMessageSummary);
                        }
                        HttpURLConnection con;
                        if (flag) {
                            // 项目编译不通过
                            try {
                                URL responseURL = new URL(requestName);
                                con = (HttpURLConnection) responseURL.openConnection();
                                con.setRequestMethod("POST");
                                con.setRequestProperty("Authorization", authorization);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("message", "Unsupported type!");
                                jsonObject.addProperty("analyseToken", analyseToken);
                                jsonObject.addProperty("code", 400);
                                jsonObject.addProperty("data_minimal", errorMessageSummary);
                                jsonObject.addProperty("data_complete", errorMessageSummary);
                                con.setDoOutput(true);
                                con.setRequestProperty("Accept", "*/*");
                                con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                                String sendMessage = jsonObject.toString();
                                OutputStream os = con.getOutputStream();
                                PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
                                out.println(sendMessage);
                                out.flush();
                                out.close();
//                            os.write(sendMessage.getBytes());
                                System.out.println(con.getResponseCode());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dependencyFileLocation != null) {
                            // 项目编译通过
                            StringBuilder dependencyTreeFileLocation = new StringBuilder();
                            dependencyTreeFileLocation.append(dependencyFileLocation, 0, dependencyFileLocation.length() - 5);
                            dependencyTreeFileLocation.append("_t.json");
                            try {
                                // formatting
                                String[] args = new String[]{"python", "component.py", dependencyTreeFileLocation.toString(), vulDBPath, analyseToken, repoName};
                                process = Runtime.getRuntime().exec(args);
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String line = null;
                                line = bufferedReader.readLine();
                                System.out.println(line);
                                bufferedReader.close();
                                process.waitFor();
                                URL responseURL = new URL(requestName);
                                con = (HttpURLConnection) responseURL.openConnection();
                                con.setRequestMethod("POST");
                                con.setRequestProperty("Authorization", authorization);
                                System.out.println(authorization);
                                con.setDoOutput(true);
                                con.setRequestProperty("Accept", "*/*");
                                con.setRequestProperty("Content-Type", "application/json");
                                OutputStream os = con.getOutputStream();
                                BufferedInputStream bin = new BufferedInputStream(Files.newInputStream(Paths.get(line)));
                                byte[] buffer = new byte[10240];
                                int length = 0;
                                while ((length = bin.read(buffer)) != -1) {
                                    System.out.println(length);
                                    os.write(buffer, 0, length);
                                }
                                os.close();
                                System.out.println(con.getResponseCode());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    HttpURLConnection con;
                    try {
                        URL responseURL = new URL(requestName);
                        con = (HttpURLConnection) responseURL.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Authorization", authorization);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("message", "Error!");
                        jsonObject.addProperty("analyseToken", analyseToken);
                        jsonObject.addProperty("code", 500);
                        jsonObject.addProperty("data_minimal", "");
                        jsonObject.addProperty("data_complete", "");
                        con.setDoOutput(true);
                        con.setRequestProperty("Accept", "*/*");
                        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        String sendMessage = jsonObject.toString();
                        OutputStream os = con.getOutputStream();
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8));
                        out.println(sendMessage);
                        out.flush();
                        out.close();
//                            os.write(sendMessage.getBytes());
                        System.out.println(con.getResponseCode());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resolveFetchFile(String prefix, JSONObject jsonObject, String accessToken) {
        String fileName = jsonObject.getStr("name");
        prefix = prefix + fileName;
        System.out.println(prefix);
        String url = jsonObject.getStr("url");
        String ret = HttpRequest.get(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "token " + accessToken)
                .execute().body();

        JSONObject parseObj = JSONUtil.parseObj(ret);
        String content = parseObj.getStr("content");
        byte[] decode = Base64.decode(content);
        cn.hutool.core.io.file.FileWriter writer = new cn.hutool.core.io.file.FileWriter(prefix);
        writer.write(new String(decode));
    }

    public void resolveFetchDir(String prefix, JSONObject jsonObject, String accessToken) {
        String dirName = jsonObject.getStr("name");
        prefix = prefix + dirName + File.separator;
        System.out.println(prefix);
        FileUtil.mkdir(prefix);
        String url = jsonObject.getStr("url");
        String ret = HttpRequest.get(url)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "token " + accessToken)
                .execute().body();

        JSONArray array = JSONUtil.parseArray(ret);

//        System.out.println(array);

        for (int i = 0; i < array.size(); i++) {
            JSONObject o = array.getJSONObject(i);
            if (o.getStr("type").equals("file")) {
                // 说明是文件
                resolveFetchFile(prefix, o, accessToken);
            } else {
                // 否则是文件夹
                resolveFetchDir(prefix, o, accessToken);
            }
        }
    }
}
