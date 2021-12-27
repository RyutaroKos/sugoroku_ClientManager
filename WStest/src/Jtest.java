import org.json.JSONObject;

public class Jtest {
	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		json.put("key", "kagi");
		json.put("God", "kami");
		json.put("TorF", false);
		String[] array = {"java","javascript","php"};
		json.put("programing",array);
		System.out.println(json);
		
		String value = json.getString("key");
		System.out.println("value = " + value);
	}
}
