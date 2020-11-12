import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        while(true) {
            String target = "https://www.naver.com/";
            HttpURLConnection con = (HttpURLConnection) new URL(target).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            FileWriter fw = null;
            String temp;

            while((temp = br.readLine()) != null) {
                if(temp.contains("svt")) { 
                    fw = new FileWriter(temp.split("svt: ")[1].split(",")[0]+".txt");

                    temp = temp.split("svt: ")[1].split(",")[0];
                    int year = Integer.parseInt(temp.substring(0, 4));
                    int month = Integer.parseInt(temp.substring(4, 6));
                    int day = Integer.parseInt(temp.substring(6, 8));
                    int hour = Integer.parseInt(temp.substring(8, 10));
                    int minute = Integer.parseInt(temp.substring(10, 12));
                    int second = Integer.parseInt(temp.substring(12, 14));

                    fw.write(year + "년 " + month + "월 " + day + "일 "
                                        + hour + "시 " + minute + "분 " + second + "초");
                }
            }
            fw.close();
            con.disconnect();
            br.close();
            Thread.sleep(10000); //10seconds
        }
    }
}
