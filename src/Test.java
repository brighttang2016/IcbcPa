import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.util.IcbcUtil;


public class Test {
	public void setM(JSONArray ja){
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			json.put("name", "");
			System.out.println("11111:"+json.toJSONString());
		}
		System.out.println(ja.toJSONString());
	}
	public static void main(String[] args) {
		Logger logger = IcbcUtil.getLogger();
		logger.info("ttttttt");
		// TODO Auto-generated method stub
		JSONArray ja = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("w", "ttttt");
		ja.add(json);
		new Test().setM(ja);
	}

}
