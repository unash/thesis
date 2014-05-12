package process;

import org.json.JSONException;
import org.json.JSONObject;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.ItemGetRequest;
import com.taobao.api.response.ItemGetResponse;

public class ClsItem {
	// 接口性质参数，周妍的appkey
	private static String appkey =ClsApi.apiAppKey;
	private static String secret =ClsApi.apiSecret;
	private static String server = ClsApi.apiServer;
	private static String sessionKey =ClsApi.apiSessionKey;

	// 保存的文件地址
	private static String apiLogFileName =ClsApi.apiLogFileName;

	// 内容类成员
	private JSONObject itemJsonObject = null;// 商品项的json对象

	public static void main(String [] args)
	{
		ClsItem ci=new ClsItem();
		ci.getItemFromApi("1");
		System.out.println(ci.getItemCidFromJsonObject());
	}
	// 获取某个品项的json数据，保留到本类中的成员变量中
	public boolean getItemFromApi(String itemId) {
		TaobaoClient client = new DefaultTaobaoClient(server, appkey,
				secret);
		ItemGetRequest req = new ItemGetRequest();
		req.setFields("num_iid,cid,title,price,desc_modules,sell_point");
		long longItemId = Long.parseLong(itemId);
		req.setNumIid(longItemId);
		// req.setTrackIid("123_track_456");
		try {
			ItemGetResponse response = client.execute(req, sessionKey);
			JSONObject json = new JSONObject(response.getBody().toString());
			if (json.has("item_get_response")) {
				itemJsonObject = json.getJSONObject("item_get_response")
						.getJSONObject("item");
				return true;
			} else {
				if (json.getJSONObject("error_response").getInt("code") == 7) {
					// 访问次数不够
				}
				return false;
			}
		} catch (Exception e) {
			System.out.println("调用API出错,品项id为：" + itemId);
			ClsFile.writeLog(apiLogFileName, "调用API出错,品项id为：" + itemId);
			//e.printStackTrace();
			itemJsonObject = null;
			return false;
		}
	}

	// 通过本类保存的json成员获取类目id
	public long getItemCidFromJsonObject() {
		if (itemJsonObject == null) {
			System.out.println("商品的json对象尚未构建！");
			return -1;
		}
		if(itemJsonObject.has("cid"))
			try {
				return itemJsonObject.getLong("cid");
			} catch (JSONException e) {
				ClsFile.writeLog(apiLogFileName, "获取商品类目出错,商品json为："
						+ itemJsonObject.toString());
				e.printStackTrace();
				return -1;
			}
		else
		{
			System.out.println("商品json对象不包含cid属性！");
			return -1;
		}
	}
}