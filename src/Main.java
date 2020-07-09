import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Main {
	
	public static JSONObject getData(String inline, String s_type) {
		JSONParser parse = new JSONParser();
		JSONObject jobj = new JSONObject();
		try {
			jobj = (JSONObject)parse.parse(inline);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONArray searchType = new JSONArray();
		searchType.add(jobj.get(s_type ));
		JSONObject top = (JSONObject)searchType.get(0);
		
		JSONArray results = new JSONArray();
		results.add(top.get("queryResults"));
		JSONObject second = (JSONObject)results.get(0);
		
		return second;
	}
	
	public static void main(String[] args) {
		String inline = "";
		
		try {
			URL url = new URL("http://lookup-service-prod.mlb.com/json/named.team_all_season.bam?sport_code='mlb'&all_star_sw='N'&sort_order=name_asc&season='2019'");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			conn.setRequestMethod("GET");
			conn.connect();
			
			int responsecode = conn.getResponseCode();
			
			if(responsecode != 200)
				throw new RuntimeException("HttpResponseCode: " +responsecode);
			else
			{
				Scanner sc = new Scanner(url.openStream());
				while(sc.hasNext())
				{
					inline+=sc.nextLine();
				}
				sc.close();
			}
			
			
			JSONArray teams = (JSONArray)getData(inline, "team_all_season").get("row");
					
			for(int i = 0; i < teams.size(); i++) {
				JSONObject team = (JSONObject)teams.get(i);
				
				conn.disconnect();
				url = new URL("http://lookup-service-prod.mlb.com/json/named.roster_team_alltime.bam?start_season='2016'&end_season='2017'&team_id=" + team.get("team_id"));
				conn = (HttpURLConnection)url.openConnection();				
				conn.setRequestMethod("GET");
				conn.connect();
				
				if(responsecode != 200)
					throw new RuntimeException("HttpResponseCode: " +responsecode);
				else
				{
					
					System.out.println("Team: " + team.get("name_display_full"));
					
					Scanner sc = new Scanner(url.openStream());
					inline = "";
					while(sc.hasNext())
					{
						inline+=sc.nextLine();
					}
					sc.close();
				};
				
				JSONArray players = (JSONArray)getData(inline, "roster_team_alltime").get("row");
				
				for(int j = 0; j < players.size(); j++) {
					JSONObject player = (JSONObject)players.get(j);
					System.out.println("        " + player.get("name_first_last"));
				} 
			}
			
			conn.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}
}