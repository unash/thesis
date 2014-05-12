package process;

import org.json.JSONException;
import org.json.JSONObject;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.ItemGetRequest;
import com.taobao.api.response.ItemGetResponse;

public class ClsItem {
	// �ӿ����ʲ�����������appkey
	private static String appkey =ClsApi.apiAppKey;
	private static String secret =ClsApi.apiSecret;
	private static String server = ClsApi.apiServer;
	private static String sessionKey =ClsApi.apiSessionKey;

	// ������ļ���ַ
	private static String apiLogFileName =ClsApi.apiLogFileName;

	// �������Ա
	private JSONObject itemJsonObject = null;// ��Ʒ���json����

	public static void main(String [] args)
	{
		ClsItem ci=new ClsItem();
		ci.getItemFromApi("1");
		System.out.println(ci.getItemCidFromJsonObject());
	}
	// ��ȡĳ��Ʒ���json���ݣ������������еĳ�Ա������
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
					// ���ʴ�������
				}
				return false;
			}
		} catch (Exception e) {
			System.out.println("����API����,Ʒ��idΪ��" + itemId);
			ClsFile.writeLog(apiLogFileName, "����API����,Ʒ��idΪ��" + itemId);
			//e.printStackTrace();
			itemJsonObject = null;
			return false;
		}
	}

	// ͨ�����ౣ���json��Ա��ȡ��Ŀid
	public long getItemCidFromJsonObject() {
		if (itemJsonObject == null) {
			System.out.println("��Ʒ��json������δ������");
			return -1;
		}
		if(itemJsonObject.has("cid"))
			try {
				return itemJsonObject.getLong("cid");
			} catch (JSONException e) {
				ClsFile.writeLog(apiLogFileName, "��ȡ��Ʒ��Ŀ����,��ƷjsonΪ��"
						+ itemJsonObject.toString());
				e.printStackTrace();
				return -1;
			}
		else
		{
			System.out.println("��Ʒjson���󲻰���cid���ԣ�");
			return -1;
		}
	}
}