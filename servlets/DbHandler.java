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

	// Frequently used strings
	private static String BAD_USER = "bad user";
	public static String USER_ATTR = "userid";
	
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
				
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select name from rider where riderid=? and password=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, id);
				preparedStmt.setString(2, password);
				ResultSet result =  preparedStmt.executeQuery();
				preparedStmt.close();
				conn.close();
				
				if (!result.isBeforeFirst() ) {    
				    System.out.println("No user found"); 
					obj.put("status", false);
					obj.put("message", "Authentication Failed");
				}
				else{
					result.next();
				
					obj.put("status",true);
					obj.put("data", id);
					
					HttpSession session = request.getSession(true);
					session.setAttribute(USER_ATTR, id);
					if(session.isNew()){
						System.out.println("New Session for "+id);
					}
					else{
						System.out.println("Using old Session for "+id);
					}
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
		
		if(userID == null) {
			try{
				obj.put("status",false);
				obj.put("message",BAD_USER);
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
			System.out.println(obj);
			return obj;
		}
		
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "with "
					+ "fullRide as "
					+ "	(select * from ride_at_stand natural join ride), "
					+ "bike as "
					+ "	(select stand.standid, count(rideid) as numbikes "
					+ "	from stand left outer join fullRide "
					+ "	on stand.standid = fullRide.standid "
					+ "	where ridetype = '2 wheeler' "
					+ "	group by stand.standid), "
					+ "otherbike as "
					+ "	(select standid,0  as numbikes "
					+ "	from stand "
					+ "	where standid not in "
					+ "		(select standid from bike)), "
					+ "allbikes as "
					+ "	(select * from bike "
					+ "	union "
					+ "	select * from otherbike), "
					+ "car as "
					+ "	(select stand.standid, count(rideid) as numcars "
					+ "	from stand left outer join fullRide "
					+ "	on stand.standid = fullRide.standid "
					+ "	where ridetype = '4 wheeler' "
					+ "	group by stand.standid), "
					+ "othercar as "
					+ "	(select standid,0  as numcars "
					+ "	from stand "
					+ "	where standid not in "
					+ "		(select standid from car)), "
					+ "allcars as "
					+ "	(select * from car "
					+ "	union "
					+ "	select * from othercar) "
					+ "select stand.standid, name, address, lat, long, numbikes, numcars "
					+ "from allbikes natural join allcars natural join stand ";
			
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
		System.out.println(obj);
		return obj;
	}
	
	public static JSONObject getRidesAtStand(String userID, String standID){		
		JSONObject obj = new JSONObject();
		System.out.println("getRidesAtStand: "+userID+", "+standID);
		String ret_cols = "ride.rideid as RideID, ridetype as Type, makemodel as Model, color as Color, url as URL, stand.name as Location";

		try{
			if (userID == null){
				obj.put("status", false);				
				obj.put("message", BAD_USER);				
			}
			else if(standID == null){
				obj.put("status", false);				
				obj.put("message", "Incomplete request");				
			}
			else {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query =  "select "+ret_cols+
						" from ride, ride_at_stand, stand" +
						" where ride.rideid=ride_at_stand.rideid and "
						+ "		ride_at_stand.standid=? and "
						+ "		ride_at_stand.standid=stand.standid and"
						+ "		not exists (select * from requests where requests.rideid=ride.rideid)";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, standID);
				ResultSet result =  preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status",true);				
				obj.put("data", stnds);	
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
	
	public static JSONObject getRide(String userID, String rideID){		
		JSONObject obj = new JSONObject();
		System.out.println("getBike: "+rideID);
		String ret_cols = "ride.rideid as RideID, ride.ridetype as Type, ride.makemodel as Model, "
				+ "	ride.color as Color, 'lent' as Status, ride.url as URL, null as Code, stand.name as Location";

		try{
			if (userID == null){
				obj.put("status", false);				
				obj.put("message", BAD_USER);				
			}
			else if(rideID == null){
				obj.put("status", false);				
				obj.put("message", "Incomplete request");				
			}
			else {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query =  "select "+ret_cols+" from ride, ride_at_stand, stand "
								+ "where ride.rideid=? and "
								+ "		ride.rideid=ride_at_stand.rideid and "
								+ "		ride_at_stand.standid=stand.standid "
								+ "		and not exists (select * from requests where requests.rideid=ride.rideid)";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, rideID);
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
	
	public static JSONObject Rent(String userid, String rideid, String standid) throws JSONException, SQLException{

		System.out.println("Rent: "+userid+", "+rideid+", "+standid);
	
		// Create the connection
		Connection conn = DriverManager.getConnection(connString, userName, passWord);
		String remove_bike_from_stand = "delete from ride_at_stand where rideid=? and standid=?";
		String save_rent_data = "insert into rentdata values(?, ?, ?, null)";
		String get_ride_code = "select code from ride where rideid=?";
		
		PreparedStatement stmt1 = conn.prepareStatement(remove_bike_from_stand);
		stmt1.setString(1, rideid);
		stmt1.setString(2, standid);
		
		PreparedStatement stmt2 = conn.prepareStatement(save_rent_data);
		stmt2.setString(1, rideid);
		stmt2.setString(2, userid);
		stmt2.setString(3, standid);
		
		JSONObject obj = new JSONObject();
		
		if (userid == null){
			obj.put("status", false);				
			obj.put("message", BAD_USER);				
		}
		else try{
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

			obj = getRideCode(userid);
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
	
	public static JSONObject Unrent(String userid, String rideid, String standid) throws JSONException, SQLException{

		System.out.println("Unrent: "+userid+", "+rideid+", "+standid);
		
		// Create the connection
		Connection conn = DriverManager.getConnection(connString, userName, passWord);
		String put_bike_in_stand = "insert into bike_at_stand values(?, ?)";
		String delete_rent_data = "delete from rentdata where bikeid=?";
		
		PreparedStatement stmt1 = conn.prepareStatement(put_bike_in_stand);
		stmt1.setString(1, rideid);
		stmt1.setString(2, standid);
		
		PreparedStatement stmt2 = conn.prepareStatement(delete_rent_data);
		stmt2.setString(1, rideid);
		
		JSONObject obj = new JSONObject();
		
		if (userid == null){
			obj.put("status", false);				
			obj.put("message", BAD_USER);				
		}
		else try{
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
			
			obj.put("status", true);
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
	
	public static JSONObject getRideCode(String RideID){
		JSONObject obj = new JSONObject();
		System.out.println("getRideCode: "+RideID);
		
		try{
			if(RideID != null) {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select code as Code from ride where rideid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, RideID);
				ResultSet result =  preparedStmt.executeQuery();
				if (!result.isBeforeFirst() ) {    
				    System.out.println("Ride Code not Found"); 
					obj.put("status", false);
					obj.put("message", "Ride Code not Found");
				}
				else{
					result.next();
					obj.put("status", false);
					obj.put("data", Integer.toString(result.getInt(1)));
				}
				preparedStmt.close();
				conn.close();
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
	
	public static JSONObject getRentedRides(String userID) throws JSONException{		
		JSONObject obj = new JSONObject();
		System.out.println("getRentedRides: "+userID);
		String ret_cols = "ride.rideid as RideID, ride.ridetype as Type, "
				+ "ride.model as Model, ride.color as ride.Color, ride.url as URL, ride.code as Code";

		if (userID == null){
			obj.put("status", false);				
			obj.put("message", BAD_USER);				
		}
		else{ 
			try {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select " + ret_cols + " from ride, rentdata " + "where ride.rideid=rentdata.rideid and "
						+ "		rentdata.userid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);
				ResultSet result = preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status", true);
				obj.put("data", stnds);
			} catch (Exception e) {
				System.out.println(e);
				try {
					obj.put("status", false);
					obj.put("message", e);
				} catch (JSONException e1) {
					System.out.println(e1);
				}
			}
		}
		return obj;
	}

	public static JSONObject getPersonalRides(String userID) throws JSONException{		
		JSONObject obj = new JSONObject();
		System.out.println("getPersonalRides: "+userID);
		String ret_cols = "ride.rideid as RideId, ride.ridetype as Type, ride.makemodel as Model, "
				+ "	ride.color as Color, ride.url as URL,"
				+ " CASE"
				+ " WHEN ride.status='with_owner' THEN '0'"
				+ "	WHEN exists (select * from requests where requests.rideid=ride.rideid and type='lend') THEN '1'"
				+ "	WHEN exists (select * from requests where requests.rideid=ride.rideid and type='unlend') THEN '3'"
				+ "	ELSE '2'"
				+ "	END as Location";

		if (userID == null){
			obj.put("status", false);				
			obj.put("message", BAD_USER);				
		}
		else{ 
			try {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select " + ret_cols + " from ride, ownership " + "where ride.rideid = ownership.rideid and ownership.ownerid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);
				ResultSet result = preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status", true);
				obj.put("data", stnds);
			} catch (Exception e) {
				System.out.println(e);
				try {
					obj.put("status", false);
					obj.put("message", e);
				} catch (JSONException e1) {
					System.out.println(e1);
				}
			}
		}
		return obj;
	}

	public static JSONObject AddBike(String userid, String name, String type, String model, String color){		
		JSONObject obj = new JSONObject();
		
		System.out.println("AddBike:"+userid+","+name+","+model+","+color);
		try{
			if (userid == null){
				obj.put("status",false);
				obj.put("message", BAD_USER);
			}
			else if (name == null){
				obj.put("status",false);
				obj.put("message", "Null name");
			}
			else if(userid.length() + name.length() > 100) {
				obj.put("status",false);
				obj.put("message", "Name too long");
			}
			else if(model == null) {
				obj.put("status",false);
				obj.put("message", "Null model");
			}
			else if(model.length() > 20){
				obj.put("status",false);
				obj.put("message", "Model name too long");
			}
			else if(color == null) {
				obj.put("status",false);
				obj.put("message", "Null color");
			}
			else if(color.length() > 20){
				obj.put("status",false);
				obj.put("message", "Color name too long");
			}
			else {
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String rel_ride = "insert into ride values(?,?,?,?,'user', 'with_owner', null, null)";
				String rel_own = "insert into ownership values(?,?)";
				
				PreparedStatement stmt1 = conn.prepareStatement(rel_ride);
				stmt1.setString(1, userid+":"+name);
				stmt1.setString(2, type);
				stmt1.setString(3, model);
				stmt1.setString(4, color);
				
				PreparedStatement stmt2 = conn.prepareStatement(rel_own);
				stmt2.setString(1, userid+":"+name);
				stmt2.setString(2, userid);
				
				try {
					conn.setAutoCommit(false);
					if(stmt1.executeUpdate() == 0) {
						obj.put("status", false);			
						obj.put("message", "Bike already exists!");
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
	
	
	public static JSONObject Lend(String userid, String rideid, String optype){
		JSONObject obj = new JSONObject();
		System.out.println("Lend: "+userid+","+rideid+","+optype);
		
		String query1 = "select count(*) from ownership where userid=? and rideid=?";
		String upd1 = "insert into requests values (?,?,?,null)";
		
		String query2 = "select * from requests where ";
		String upd2 = "delete from requests where ride"
		String ret_cols = "ride.rideid as RideId, ride.ridetype as Type, ride.makemodel as Model, "
				+ "	ride.color as Color, ride.url as URL,"
				+ " CASE"
				+ " WHEN ride.status='with_owner' THEN '0'"
				+ "	WHEN exists (select * from requests where requests.rideid=ride.rideid and type='lend') THEN '1'"
				+ "	WHEN exists (select * from requests where requests.rideid=ride.rideid and type='unlend') THEN '3'"
				+ "	ELSE '2'"
				+ "	END as Location";

		if (userID == null){
			obj.put("status", false);				
			obj.put("message", BAD_USER);				
		}
		else{ 
			try {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select " + ret_cols + " from ride, ownership " + "where ride.rideid = ownership.rideid and ownership.ownerid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);
				ResultSet result = preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status", true);
				obj.put("data", stnds);
			} catch (Exception e) {
				System.out.println(e);
				try {
					obj.put("status", false);
					obj.put("message", e);
				} catch (JSONException e1) {
					System.out.println(e1);
				}
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

	public static JSONObject register(HttpServletRequest request, String id, String name, String password){		
		JSONObject obj = new JSONObject();
		System.out.println("register:"+id+","+name+","+password);
		try{
			if(id == null) {
				obj.put("status",false);
				obj.put("message", "Null email");
			}
			else if(name == null) {
				obj.put("status",false);
				obj.put("message", "Null name");
			}
			else if(password == null) {
				obj.put("status",false);
				obj.put("message", "Null password");
			}
			else if(id.length()>50) {
				obj.put("status",false);
				obj.put("message", "Email too long");
			}
			else if(name.length()>40) {
				obj.put("status",false);
				obj.put("message", "Name too long");
			}
			else if(password.length()>20) {
				obj.put("status",false);
				obj.put("message", "Password too long");
			}
			else {
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "insert into rider values(?,?,?,null,null)";
		
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1, id);
				stmt.setString(2, name);
				stmt.setString(2, password);
				if(stmt.executeUpdate() == 0) {
					obj.put("status", false);			
					obj.put("message", "Email already registered");
					conn.close();
					return obj;
				}
				else {
					obj.put("status", true);			
					obj.put("message", "");
					obj.put("data", name);
					
					HttpSession session = request.getSession(true);
					session.setAttribute(DbHandler.USER_ATTR, id);
//					if(session.isNew()){
//						System.out.println("New Session for "+id);
//					}
//					else{
//						System.out.println("Using old Session for "+id);
//					}
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

}