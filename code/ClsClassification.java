package process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.ItemcatsGetRequest;
import com.taobao.api.response.ItemcatsGetResponse;

public class ClsClassification {
	// ȫ����Ϣ
	private static String classesJsonFileName = "data/Taxonomy.json";// ���෨�ļ�
	private static String classesJsonFileCharacter = "GB2312";
	// �ӿ����ʲ�����������appkey
	private static String appkey =ClsApi.apiAppKey;
	private static String secret =ClsApi.apiSecret;
	private static String server = ClsApi.apiServer;
	private static String sessionKey =ClsApi.apiSessionKey;

	// ʵ����Ϣ
	private HashMap<Integer, Integer> classesMapNodeToRoot = null;// ���ӽڵ�����ڵ�
	private HashMap<Integer, String> classesMapCidToCname = null;// ���ӽڵ�����ڵ�
	private JSONObject classesJsonObject = null;// ������ṹ��json����

	public static void main(String[] args) {
		ClsClassification cc = new ClsClassification();
		cc.initClassesJsonFromFile(classesJsonFileName, classesJsonFileCharacter);
		cc.produceMapCidToCname();
		cc.produceMapNodeToRoot();
		System.out.println(cc.getCnameFromCid(-1));
		System.out.println(cc.getRootFromNode(-1));
		System.out.println("asdf");
	}
	
	public ClsClassification()
	{
		initClassesJsonFromFile(classesJsonFileName, classesJsonFileCharacter);
		produceMapNodeToRoot();
		produceMapCidToCname();
	}
	
	// ��ȡjson�ļ��б��������ϵ��json���󣬳ɹ��򷵻�true
	public boolean initClassesJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			classesJsonObject = new JSONObject(jsonTokerner);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	// ͨ��api��ȡ��Ŀjson��ʧ�ܷ���false
	public boolean initClassesFromApi(String ancient)// ��Api�л����ṹ
	{
		System.out.println("����ͨ��API��ȡ��Ŀ��ϵ...");
		TaobaoClient client = new DefaultTaobaoClient(server, appkey,
				secret);
		ItemcatsGetRequest req = new ItemcatsGetRequest();
		// req.setFields("cid,parent_cid,name,is_parent");
		req.setCids(ancient);
		classesJsonObject = null;
		ItemcatsGetResponse response;
		try {
			response = client.execute(req, sessionKey);
			JSONObject json = new JSONObject(response.getBody().toString());
			classesJsonObject = (JSONObject) json
					.getJSONObject("itemcats_get_response")
					.getJSONObject("item_cats").getJSONArray("item_cat").get(0);
			// System.out.println(root.toString());
			classesJsonObject = initClassesFromApiSub(classesJsonObject);
			// System.out.println(root.toString());
			return true;
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// ��ȡ�����࣬�Ӻ���
	private JSONObject initClassesFromApiSub(JSONObject jsonParent) {
		long parentId = 0;
		try {
			parentId = jsonParent.getLong("cid");
			TaobaoClient client = new DefaultTaobaoClient(server, appkey,
					secret);
			ItemcatsGetRequest req = new ItemcatsGetRequest();
			req.setParentCid(parentId);
			ItemcatsGetResponse response;
			response = client.execute(req, sessionKey);
			JSONObject json = new JSONObject(response.getBody().toString());
			JSONArray children = json.getJSONObject("itemcats_get_response")
					.getJSONObject("item_cats").getJSONArray("item_cat");
			for (int i = 0; i < children.length(); i++) {
				JSONObject child = (JSONObject) children.get(i);
				if (child.getBoolean("is_parent")) {
					child = initClassesFromApiSub(child);
				}
			}
			jsonParent.put("children", children);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonParent;
	}

	public boolean produceMapNodeToRoot()// �����ӽڵ�����ڵ��ӳ���
	{
		if (classesJsonObject == null) {
			return false;
		}
		classesMapNodeToRoot = new HashMap<Integer, Integer>();
		// ʹ�õݹ�
		JSONArray rootClassesList;
		int rootCid = -1;
		try {
			rootClassesList = classesJsonObject.getJSONArray("children");
			for (int i = 0; i < rootClassesList.length(); i++) {
				rootCid = rootClassesList.getJSONObject(i).getInt("cid");
				produceMapNodeToRootSub(rootCid,
						rootClassesList.getJSONObject(i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private boolean produceMapNodeToRootSub(Integer rootCid, JSONObject json)// �����ӽڵ�����ڵ��ӳ����Ӻ���
	{
		try {
			if (json.has("cid")) {
				classesMapNodeToRoot.put(json.getInt("cid"), rootCid);
				if (json.has("is_parent")
						&& json.getBoolean("is_parent") == true) {
					JSONArray jsonChildrenClasses = json
							.getJSONArray("children");
					for (int i = 0; i < jsonChildrenClasses.length(); i++) {
						produceMapNodeToRootSub(rootCid,
								jsonChildrenClasses.getJSONObject(i));
					}
				}
			}
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean produceMapCidToCname() {
		if (classesJsonObject == null) {
			return false;
		}
		classesMapCidToCname = new HashMap<Integer, String>();
		prodeceMapCidToCnameSub(classesJsonObject);
		return true;
	}

	private boolean prodeceMapCidToCnameSub(JSONObject jsonParent) {
		int intCid = 0;
		String strCname = "";
		try {
			if (jsonParent != null) {
				if (jsonParent.has("cid") && jsonParent.has("name")) {
					intCid = jsonParent.getInt("cid");
					strCname = jsonParent.getString("name");
					classesMapCidToCname.put(intCid, strCname);
				}
				if (jsonParent.getBoolean("is_parent")) {
					JSONArray jsonChildren = jsonParent
							.getJSONArray("children");
					for (int i = 0; i < jsonChildren.length(); i++) {
						JSONObject child = (JSONObject) jsonChildren.get(i);
						prodeceMapCidToCnameSub(child);
					}
				}
			}
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// ����id�л�ȡ��name
	public String getCnameFromCid(int cid) {
		if (classesMapCidToCname == null) {
			System.out.println("��Ŀ����ӳ��û�и��");
			return null;
		} else {
			if (classesMapCidToCname.containsKey(cid)) {
				String cname = (String) classesMapCidToCname.get(cid);
				return cname;
			} else {
				System.out.print("��Ŀ��ϵ�в�����cidΪ " + cid + " �Ľڵ�");
				return null;
			}
		}
	}

	// ����id�л�ȡ��name
	public int getRootFromNode(int nodeClassId) {
		if (classesMapNodeToRoot == null) {
			System.out.println("��Ŀ����ӳ��û�и��");
			return -1;
		} else {
			if (classesMapNodeToRoot.containsKey(nodeClassId)) {
				int rootClassId = (int) classesMapNodeToRoot.get(nodeClassId);
				return rootClassId;
			} else {
				System.out.print("��Ŀ��ϵ�в�����cidΪ " + nodeClassId + " �Ľڵ�");
				return -1;
			}
		}
	}

	// ��json��Ŀд���ļ�
	public void writeClassesToJsonFile(String fileName) {
		if (classesJsonObject != null) {
			ClsFile cf = new ClsFile(fileName);
			cf.writeFile(classesJsonObject.toString());
			cf.closeFile();
			System.out.println("��Ŀ��ϵ��json�ļ�д����ϣ��������ļ� " + fileName + " ��");
		}
	}

	// ��json���͵�����ϵ��tab�㼶�ĸ�ʽ��ӡ������������
	public void writeClassesToTabFile(String fileName) {
		if (classesJsonObject != null) {
			ClsFile cf = new ClsFile(fileName);
			writeClassesToTabFileSub(0, classesJsonObject, cf);
			cf.closeFile();
			System.out.println("��Ŀ��ϵ��tab�ļ�д����ϣ��������ļ� " + fileName + " ��");
		}
	}

	// ��json���͵�����ϵ��tab�㼶�ĸ�ʽ��ӡ�������Ӻ���
	private boolean writeClassesToTabFileSub(int tabNum,
			JSONObject jsonChildClasses, ClsFile cf) {
		try {
			String line = "";
			for (int j = 0; j < tabNum; j++) {
				line += "\t";
			}
			if (jsonChildClasses.has("name")) {
				cf.writeFile(line + jsonChildClasses.getString("name") + "\n");
			} else {
				cf.writeFile(line + "[������Ŀ]" + "\n");
			}
			if (jsonChildClasses.has("children")) {
				JSONArray children = jsonChildClasses.getJSONArray("children");
				for (int i = 0; i < children.length(); i++) {
					JSONObject child = (JSONObject) children.get(i);
					writeClassesToTabFileSub(tabNum + 1, child, cf);
				}
			}
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
