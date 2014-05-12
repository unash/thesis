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
	// 全局信息
	private static String classesJsonFileName = "data/Taxonomy.json";// 分类法文件
	private static String classesJsonFileCharacter = "GB2312";
	// 接口性质参数，周妍的appkey
	private static String appkey =ClsApi.apiAppKey;
	private static String secret =ClsApi.apiSecret;
	private static String server = ClsApi.apiServer;
	private static String sessionKey =ClsApi.apiSessionKey;

	// 实例信息
	private HashMap<Integer, Integer> classesMapNodeToRoot = null;// 从子节点向根节点
	private HashMap<Integer, String> classesMapCidToCname = null;// 从子节点向根节点
	private JSONObject classesJsonObject = null;// 保存类结构的json对象

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
	
	// 获取json文件中保存的类体系到json对象，成功则返回true
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

	// 通过api获取类目json，失败返回false
	public boolean initClassesFromApi(String ancient)// 从Api中获得类结构
	{
		System.out.println("正在通过API获取类目体系...");
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

	// 获取所有类，子函数
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

	public boolean produceMapNodeToRoot()// 产生子节点向根节点的映射表
	{
		if (classesJsonObject == null) {
			return false;
		}
		classesMapNodeToRoot = new HashMap<Integer, Integer>();
		// 使用递归
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

	private boolean produceMapNodeToRootSub(Integer rootCid, JSONObject json)// 产生子节点向根节点的映射表子函数
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

	// 从类id中获取类name
	public String getCnameFromCid(int cid) {
		if (classesMapCidToCname == null) {
			System.out.println("类目名称映射没有搞好");
			return null;
		} else {
			if (classesMapCidToCname.containsKey(cid)) {
				String cname = (String) classesMapCidToCname.get(cid);
				return cname;
			} else {
				System.out.print("类目体系中不包括cid为 " + cid + " 的节点");
				return null;
			}
		}
	}

	// 从类id中获取类name
	public int getRootFromNode(int nodeClassId) {
		if (classesMapNodeToRoot == null) {
			System.out.println("类目名称映射没有搞好");
			return -1;
		} else {
			if (classesMapNodeToRoot.containsKey(nodeClassId)) {
				int rootClassId = (int) classesMapNodeToRoot.get(nodeClassId);
				return rootClassId;
			} else {
				System.out.print("类目体系中不包括cid为 " + nodeClassId + " 的节点");
				return -1;
			}
		}
	}

	// 将json类目写入文件
	public void writeClassesToJsonFile(String fileName) {
		if (classesJsonObject != null) {
			ClsFile cf = new ClsFile(fileName);
			cf.writeFile(classesJsonObject.toString());
			cf.closeFile();
			System.out.println("类目体系的json文件写入完毕，保存在文件 " + fileName + " 中");
		}
	}

	// 将json类型的类体系用tab层级的格式打印出来，父函数
	public void writeClassesToTabFile(String fileName) {
		if (classesJsonObject != null) {
			ClsFile cf = new ClsFile(fileName);
			writeClassesToTabFileSub(0, classesJsonObject, cf);
			cf.closeFile();
			System.out.println("类目体系的tab文件写入完毕，保存在文件 " + fileName + " 中");
		}
	}

	// 将json类型的类体系用tab层级的格式打印出来，子函数
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
				cf.writeFile(line + "[无名类目]" + "\n");
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
