package process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClsPage {
	// ����ͳ�Ƶĳ�Ա
	private Map<String, Integer> params = null;
	public int pageFreq;
	
	//���캯��
	public ClsPage(){
		params = new HashMap<String, Integer>();// ��ʼ��ͳ��
		pageFreq = 1;
	}
	//Ϊҳ�����һ������
	public boolean getParam(String param){
		if(params.containsKey(param)){
			int paramFreq=params.get(param)+1;
			params.remove(param);
			params.put(param, paramFreq);
			return false;
		}
		else{
			params.put(param, 1);
			return true;
		}
	}
	public String serializeParamsInfo() {
		String line = "";
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				params.entrySet());
		// ��HashMap�е� value ��������
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue()).toString().compareTo(
						o1.getValue().toString());
			}
		});
		line+=pageFreq+"\t";
		line+=infoIds.size();
		// ��HashMap�е� value ��������� ��ʾ������
		for (int i = 0; i < infoIds.size(); i++) {
			String id = infoIds.get(i).toString();
			line+="\t"+id;
		}
		return line;
	}
}
