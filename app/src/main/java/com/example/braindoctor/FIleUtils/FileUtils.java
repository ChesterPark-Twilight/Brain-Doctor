package com.example.braindoctor.FIleUtils;

import android.content.Context;

public class FileUtils {
    public static final String[] TYPE = new String[]{"t1", "t1ce", "t2", "flair", "seg"};

    public static String getNiiFilePath(String uid, String type, Context context) {
        return context.getFilesDir().getAbsolutePath() + "/" + uid + type + ".nii.gz";
    }

//    public static List<List<List<Integer>>> handleJson(Context context) throws JSONException {
//        String modelJson = "";
//        try {
//            InputStream inputStream = context.getResources().openRawResource(R.raw.model);
//            Writer writer = new StringWriter();
//            char[] buffer = new char[inputStream.available()];
//            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//            modelJson = writer.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.d("读取", "开始");
//        JSONObject jsonObject = new JSONObject(modelJson);
//        Log.d("读取", "读取结束");
//
//        Log.d("建表", "开始");
//        List<List<List<Integer>>> dataList = new ArrayList<>();
//        JSONArray spaceArray = jsonObject.getJSONArray("data");
//        for (int z=0 ; z<240 ; z++) {
//            List<List<Integer>> planeList = new ArrayList<>();
//            JSONArray planeArray = new JSONArray(spaceArray.getString(z));
//            for (int y=0 ; y<240 ; y++) {
//                String lineArrayString = planeArray.getString(y);
//                lineArrayString = lineArrayString.substring(1, lineArrayString.length()-1);
//                String[] lineData = lineArrayString.split(",");
//                List<Integer> lineList = new ArrayList<>();
//                for (String data : lineData) {
//                    lineList.add(Integer.parseInt(data));
//                }
//                planeList.add(lineList);
//            }
//            dataList.add(planeList);
//        }
//        Log.d("读取", "建表结束");
//        Log.d("数据", String.valueOf(dataList.size()));
//        Log.d("数据", String.valueOf(dataList.get(0).size()));
//        Log.d("数据", String.valueOf(dataList.get(0).get(0).size()));
//        Intent intent = new Intent().setAction("com.example.braindoctor.loadSuccess");
//        context.sendBroadcast(intent);
//        return dataList;
//    }

}
