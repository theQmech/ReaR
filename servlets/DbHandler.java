package main;

import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbHandler {
	// connection strings
	private static String connString = "jdbc:postgresql://localhost:5432/postgres";
	private static String userName = "rganvir";
	private static String passWord = "";
	private static String ADMIN_ID = "000";
	private static String ADMIN_PWD = "pwd";
	
	private static JSONArray ResultSetConverter(ResultSet rs) throws SQLException, JSONException {
		
		// TODO Auto-generated method stub
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
	    while(rs.next()) {
	        int numColumns = rsmd.getColumnCount();
	        JSONObject obj = new JSONObject();

	        for (int i=1; i<numColumns+1; i++) {
	          String column_name = rsmd.getColumnName(i);

	          if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
	           obj.put(column_name, rs.getArray(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
	           obj.put(column_name, rs.getBoolean(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
	           obj.put(column_name, rs.getBlob(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
	           obj.put(column_name, rs.getDouble(column_name)); 
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
	           obj.put(column_name, rs.getFloat(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
	           obj.put(column_name, rs.getNString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
	           obj.put(column_name, rs.getString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
	           obj.put(column_name, rs.getDate(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
	          obj.put(column_name, rs.getTimestamp(column_name));   
	          }
	          else{
	           obj.put(column_name, rs.getObject(column_name));
	          }
	        }

	        json.put(obj);
	      }
	    return json;
	}
	
	public static JSONObject authenticate(HttpServletRequest request, String id, String password){		
		JSONObject obj = new JSONObject();
		System.out.println("authenticate:"+id+","+password);
		try{
			if(id == null) {
				obj.put("status",false);
				obj.put("message", "Null id");
			}
			else if(password == null) {
				obj.put("status",false);
				obj.put("message", "Null password");
			}
			else {
				if(id.equals(ADMIN_ID) && password.equals(ADMIN_PWD)) {
					obj.put("status",true);			
					obj.put("message", "admin");		
					obj.put("data", id);
				}
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select count(*) from rider where riderid=? and password=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, id);
				preparedStmt.setString(2, password);
				ResultSet result =  preparedStmt.executeQuery();
				result.next();
				boolean ans = (result.getInt(1) > 0); 
				preparedStmt.close();
				conn.close();
				if(ans==true){
					obj.put("status",true);			
					obj.put("message", "user");		
					obj.put("data", id);
					
					HttpSession session = request.getSession(true);
					session.setAttribute("userid", id);
					if(session.isNew()){
						System.out.println("New Session for "+id);
					}
					else{
						System.out.println("Using old Session for "+id);
					}
				}
				else{						
						obj.put("status",false);
						obj.put("message", "Authentication Failed");					
				}
			}			
		} 
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		return obj;
	}

	public static JSONObject getStands(String userID){		
		JSONObject obj = new JSONObject();
		System.out.println("getStands");
		String ret_cols = " stand.standid as StandID, address as Address, location as Location, count(rideid) as NumRides ";

		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select "+ret_cols+" from stand left outer join ride_at_stand "
					+ "on stand.standid = ride_at_stand.standid "
					+ "group by (stand.standid)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet result =  preparedStmt.executeQuery();
			
			JSONArray stnds = ResultSetConverter(result);
			preparedStmt.close();
			conn.close();
			
			obj.put("status",true);				
			obj.put("data", stnds);		
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		return obj;
	}
	
	public static JSONObject getBikeList(String userID, String standID, String showroomID){		
		JSONObject obj = new JSONObject();
		System.out.println("getBikeList: "+userID+", "+standID+", "+showroomID);
		String ret_cols = " bikeid as BikeID,makemodel as Model,color as Color,status as Status ";

		try{
			if(showroomID != null) {
				obj.put("status",false);
				obj.put("message", "Not Implemented showrooms yet.");
			}
			else if(standID != null) {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query =  "select "+ret_cols+" from bike natural join bike_at_stand where bikestandid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, standID);
				ResultSet result =  preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status",true);				
				obj.put("data", stnds);	
			}
			else if(userID != null) {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query =  "select "+ret_cols+" from bike natural join ownership where ownerid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);
				ResultSet result =  preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status",true);				
				obj.put("data", stnds);
			} 
			else {
				obj.put("status",false);
				obj.put("message","Null Arguments");
			}		
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		return obj;
	}
	
	public static JSONObject getBike(String bikeID){		
		JSONObject obj = new JSONObject();
		System.out.println("getBike: "+bikeID);
		String ret_cols = " bikeid as BikeID,makemodel as Model,color as Color,status as Status ";

		try{
			if(bikeID != null) {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query =  "select "+ret_cols+" from bike where bikeid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, bikeID);
				ResultSet result =  preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();
				
				if(stnds.length()<1) {
					obj.put("status",false);
					obj.put("message","Bike not found");
				}
				else {
					obj.put("status",true);				
					obj.put("data", stnds.get(0));
				}
			} 
			else {
				obj.put("status",false);
				obj.put("message","Null Arguments");
			}		
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		return obj;
	}
	
public static JSONObject Rent(String userid, String bikeid, String standid) throws JSONException, SQLException{

		System.out.println("Rent: "+userid+", "+bikeid+", "+standid);
	
		// Create the connection
		Connection conn = DriverManager.getConnection(connString, userName, passWord);
		String remove_bike_from_stand = "delete from bike_at_stand where bikeid=? and bikestandid=?";
		String save_rent_data = "insert into rentdata values(?, ?, ?, null)";
		
		PreparedStatement stmt1 = conn.prepareStatement(remove_bike_from_stand);
		stmt1.setString(1, bikeid);
		stmt1.setString(2, standid);
		
		PreparedStatement stmt2 = conn.prepareStatement(save_rent_data);
		stmt2.setString(1, bikeid);
		stmt2.setString(2, userid);
		stmt2.setString(3, standid);
		
		JSONObject obj = new JSONObject();
		
		try{
			//Assume a valid connection object conn
			try {
				conn.setAutoCommit(false);
				if(stmt1.executeUpdate() == 0) {
					obj.put("status", false);			
					obj.put("message", "Bike not present at stand");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					return obj;
				}
				stmt2.executeUpdate();
				conn.commit();
			}
			catch(SQLException se) {
				conn.rollback();
			}
			
			conn.setAutoCommit(true);
			conn.close();

			obj = getRentedBikeList(userid);
		}
		catch(SQLException se){
			conn.close();
		   // If there is any error.
			obj.put("status", false);			
			obj.put("message", "Rent Operation failed. Refresh. Bike may no longer be available");
			System.out.println(obj.toString());
		}
		
		
		return obj;	
	}
	
	public static JSONObject Unrent(String userid, String bikeid, String standid) throws JSONException, SQLException{

		System.out.println("Unrent: "+userid+", "+bikeid+", "+standid);
		
		// Create the connection
		Connection conn = DriverManager.getConnection(connString, userName, passWord);
		String put_bike_in_stand = "insert into bike_at_stand values(?, ?)";
		String delete_rent_data = "delete from rentdata where bikeid=?";
		
		PreparedStatement stmt1 = conn.prepareStatement(put_bike_in_stand);
		stmt1.setString(1, bikeid);
		stmt1.setString(2, standid);
		
		PreparedStatement stmt2 = conn.prepareStatement(delete_rent_data);
		stmt2.setString(1, bikeid);
		
		JSONObject obj = new JSONObject();
		
		try{
			//Assume a valid connection object conn
			try {
				conn.setAutoCommit(false);
				if(stmt2.executeUpdate() == 0) {
					obj.put("status", false);			
					obj.put("message", "Bike not rented");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					return obj;
				}
				stmt1.executeUpdate();
				conn.commit();
			}
			catch(SQLException se) {
				conn.rollback();
			}
			
			conn.setAutoCommit(true);
			conn.close();
			
			obj = getRentedBikeList(userid);
		}
		catch(SQLException se){
			conn.close();
		   // If there is any error.
			obj.put("status", false);			
			obj.put("message", "Rent Operation failed. Refresh. Bike may no longer be available");
			System.out.println(obj.toString());
		}
		
		
		return obj;	
	}
	
	public static JSONObject getRentedBikeList(String userID){		
		JSONObject obj = new JSONObject();
		System.out.println("getRentedBikeList: "+userID);
		String ret_cols = " bikeid as BikeID,makemodel as Model,color as Color,status as Status ";

		try{
			if(userID != null) {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query =  "select "+ret_cols+" from bike natural join rentdata where userid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);
				ResultSet result =  preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status",true);				
				obj.put("data", stnds);
			} 
			else {
				obj.put("status",false);
				obj.put("message","Null Arguments");
			}		
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		return obj;
	}
	
	public static boolean bike_present_at_stand(String bikeID, String standID){	
		System.out.println("authenticate:"+bikeID+","+standID);
		try{
			if(bikeID == null || standID == null) {
				return false;
			}
			else {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select count(*) from bike_at_stand where bikeID=? and bikestandid=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, bikeID);
				preparedStmt.setString(2, standID);
				ResultSet result =  preparedStmt.executeQuery();
				result.next();
				boolean ans = (result.getInt(1) > 0); 
				preparedStmt.close();
				conn.close();
				return ans;
			}			
		} 
		catch(Exception e) {
			System.out.println(e);
		}
		return false;
	}

}