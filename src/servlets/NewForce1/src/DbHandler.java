
import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbHandler {
	// connection strings
	private static String connString = "jdbc:postgresql://localhost:5860/postgres";
	private static String userName = "shubham";
	private static String passWord = "";

	private static String admin = "admin";
	private static String admin_pass = "qwertyuiop";
	
	// Frequently used strings
	public static String BAD_USER = "bad user";
	public static String USER_ATTR = "userid";
	public static String IS_ADMIN = "IS_ADMIN";
	
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
				obj.put("data", "");
			}
			else if(password == null) {
				obj.put("status",false);
				obj.put("message", "Null password");
				obj.put("data", "");
			}
			else {

				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select name, riderid from rider where riderid=? and password=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, id);
				preparedStmt.setString(2, password);
				ResultSet result =  preparedStmt.executeQuery();

				if (!result.isBeforeFirst() ) {
					System.out.println("No user found");
					obj.put("status", false);
					obj.put("message", "Authentication Failed");
					obj.put("data", "");
				}
				else{
					result.next();

					JSONObject temp = new JSONObject();
					temp.put("riderid", result.getString(2));
					temp.put("name", result.getString(1));		
					obj.put("status",true);
					obj.put("message", "");
					obj.put("data", temp);

					HttpSession session = request.getSession(true);
					session.setAttribute(USER_ATTR, id);
					if(session.isNew()){
						System.out.println("New Session for "+id);
					}
					else{
						System.out.println("Using old Session for "+id);
					}
				}
				preparedStmt.close();
				conn.close();
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject getStands(String userID){
		JSONObject obj = new JSONObject();
		System.out.println("getStands");

		if(userID == null) {
			try{
				obj.put("status",false);
				obj.put("message",BAD_USER);
				obj.put("data", "");
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
			obj.put("message", "");
			obj.put("data", stnds);
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
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
				obj.put("data", "");
			}
			else if(standID == null){
				obj.put("status", false);
				obj.put("message", "Incomplete request");
				obj.put("data", "");
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
				obj.put("message", "");
				obj.put("data", stnds);
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
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
				obj.put("data", "");
			}
			else if(rideID == null){
				obj.put("status", false);
				obj.put("message", "Incomplete request");
				obj.put("data", "");
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
					obj.put("data", "");
				}
				else {
					obj.put("status",true);
					obj.put("message", "");
					obj.put("data", stnds.get(0));
				}
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject Rent(String userid, String rideid, String standid) {
		JSONObject obj = new JSONObject();
		System.out.println("Rent: "+userid+", "+rideid+", "+standid);
		standid = bike_present_at_stand(rideid);

		try {
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String remove_bike_from_stand = "delete from ride_at_stand where rideid=? and standid=?";
			String save_rent_data = "insert into rentdata values(?, ?, ?, null)";
			String get_ride_code = "select code from ride where rideid=?";
			String save_history = "insert into history values(?, ?, ?, now(), null, null)";
			
			PreparedStatement stmt1 = conn.prepareStatement(remove_bike_from_stand);
			stmt1.setString(1, rideid);
			stmt1.setString(2, standid);

			PreparedStatement stmt2 = conn.prepareStatement(save_rent_data);
			stmt2.setString(1, rideid);
			stmt2.setString(2, userid);
			stmt2.setString(3, standid);

			PreparedStatement stmt3 = conn.prepareStatement(get_ride_code);
			stmt3.setString(1, rideid);

			PreparedStatement stmt4 = conn.prepareStatement(save_history);
			stmt4.setString(1, rideid);
			stmt4.setString(2, userid);
			stmt4.setString(3, standid);

			
			if (userid == null){
				obj.put("status", false);
				obj.put("message", BAD_USER);
				obj.put("data", "");
			}
			else if(standid == null) {
				obj.put("status", false);
				obj.put("message", "Bike not Available!");
				obj.put("data", "");
			}
			else {
				//Assume a valid connection object conn
				try {
					conn.setAutoCommit(false);
					if(stmt1.executeUpdate() == 0) {
						obj.put("status", false);
						obj.put("message", "Bike not present at stand");
						obj.put("data", "");
						conn.rollback();
						conn.setAutoCommit(true);
						conn.close();
						System.out.println(obj);
						return obj;
					}
					stmt2.executeUpdate();
					stmt4.executeUpdate();
					ResultSet result = stmt3.executeQuery();
					if(result.next()) {
						obj.put("status", true);
						obj.put("message", "");
						obj.put("data", result.getString(1));
						conn.commit();
						conn.close();
						System.out.println(obj);
						return obj;
					}
					else {
						obj.put("status", false);
						obj.put("message", "Bike not available!");
						obj.put("data", "");
						conn.rollback();
						conn.setAutoCommit(true);
						conn.close();
						System.out.println(obj);
						return obj;
					}
				}
				catch(SQLException se) {
					conn.rollback();

					obj.put("status", false);
					obj.put("message", se);
					obj.put("data", "");
				}
				
				conn.setAutoCommit(true);
				conn.close();
				System.out.println(obj.toString());
				return obj;
			}

		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject Unrent(String userid, String rideid, String standid) {

		JSONObject obj = new JSONObject();
		System.out.println("Unrent: "+userid+", "+rideid+", "+standid);

		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String put_bike_in_stand = "insert into ride_at_stand values(?, ?)";
			String delete_rent_data = "delete from rentdata where rideid=?";
			String save_history = "update history set totime=now(), tostandid=? where rideid=? and tostandid is null";

			PreparedStatement stmt1 = conn.prepareStatement(put_bike_in_stand);
			stmt1.setString(1, rideid);
			stmt1.setString(2, standid);

			PreparedStatement stmt2 = conn.prepareStatement(delete_rent_data);
			stmt2.setString(1, rideid);

			PreparedStatement stmt3 = conn.prepareStatement(save_history);
			stmt3.setString(1, standid);
			stmt3.setString(2, rideid);

			if (userid == null){
				obj.put("status", false);
				obj.put("message", BAD_USER);
				obj.put("data", "");
			}
			else try{
				//Assume a valid connection object conn
				try {
					conn.setAutoCommit(false);
					if(stmt2.executeUpdate() == 0) {
						obj.put("status", false);
						obj.put("message", "Bike not rented");
						obj.put("data", "");
						conn.rollback();
						conn.setAutoCommit(true);
						conn.close();
						System.out.println(obj);
						return obj;
					}
					stmt1.executeUpdate();
					stmt3.executeUpdate();
					conn.commit();

					obj.put("status", true);
					obj.put("message", "");
					obj.put("data", "");
				}
				catch(SQLException se) {
					conn.rollback();

					obj.put("status", false);
					obj.put("message", se);
					obj.put("data", "");
				}

				conn.setAutoCommit(true);
				conn.close();
			}
			catch(SQLException se){
				conn.close();
			   // If there is any error.
				obj.put("status", false);
				obj.put("message", "UnRent Operation failed. Refresh. Bike may no longer be available");
				obj.put("data", "");

				conn.setAutoCommit(true);
				conn.close();
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
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
					obj.put("data", "");
				}
				else{
					result.next();
					obj.put("status", false);
					obj.put("message", "");
					obj.put("data", Integer.toString(result.getInt(1)));
				}
				preparedStmt.close();
				conn.close();
			}
			else {
				obj.put("status",false);
				obj.put("message","Null Arguments");
				obj.put("data", "");
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject getRentedRides(String userID) {
		JSONObject obj = new JSONObject();
		System.out.println("getRentedRides: "+userID);
		String ret_cols = "ride.rideid as RideID, ride.ridetype as Type, "
				+ "ride.makemodel as Model, ride.color as Color, ride.url as URL, ride.code as Code";

		try {
			if (userID == null){
				obj.put("status", false);
				obj.put("message", BAD_USER);
				obj.put("data", "");
			}
			else{
				try {
					// Create the connection
					Connection conn = DriverManager.getConnection(connString, userName, passWord);
					String query = "select " + ret_cols + " from ride, rentdata " + "where ride.rideid=rentdata.rideid and "
							+ "		rentdata.userid=?";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setString(1, userID);
					
					System.out.println(preparedStmt.toString());
					
					ResultSet result = preparedStmt.executeQuery();
					JSONArray stnds = ResultSetConverter(result);
					preparedStmt.close();
					conn.close();

					obj.put("status", true);
					obj.put("message", "");
					obj.put("data", stnds);
				} catch (Exception e) {
					System.out.println(e);
					try {
						obj.put("status", false);
						obj.put("message", e);
						obj.put("data", "");
					} catch (JSONException e1) {
						System.out.println(e1);
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject getPersonalRides(String userID) {
		JSONObject obj = new JSONObject();
		System.out.println("getPersonalRides: "+userID);
		String ret_cols = "ride.rideid as RideId, ride.ridetype as Type, ride.makemodel as Model, "
				+ "	ride.color as Color, ride.url as URL,"
				+ " CASE"
				+ "	WHEN exists (select * from requests where requests.rideid=ride.rideid and type='lend') THEN '1'"
				+ "	WHEN exists (select * from requests where requests.rideid=ride.rideid and type='unlend') THEN '3'"
				+ " WHEN ride.status='with_owner' THEN '0'"
				+ "	ELSE '2'"
				+ "	END as Status";


		try {
			if (userID == null){
					obj.put("status", false);
					obj.put("message", BAD_USER);
					obj.put("data", "");
			}
			else{
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select " + ret_cols + " from ride, ownership " + "where ride.rideid = ownership.rideid and ownership.ownerid=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);

				//System.out.println(preparedStmt.toString());

				ResultSet result = preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status", true);
				obj.put("message", "");
				obj.put("data", stnds);
			}
		} catch (Exception e) {
			System.out.println(e);
			try {
				obj.put("status", false);
				obj.put("message", e);
				obj.put("data", "");
			} catch (JSONException e1) {
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject AddBike(String userid, String type, String model, String color){
		JSONObject obj = new JSONObject();

		System.out.println("AddBike:"+userid+","+type+","+model+","+color);
		try{
			if (userid == null){
				obj.put("status",false);
				obj.put("message", BAD_USER);
				obj.put("data", "");
			}
			else if(type == null) {
				obj.put("status",false);
				obj.put("message", "Null type");
				obj.put("data", "");
			}
			else if(!type.equals("2 wheeler") && !type.equals("4 wheeler")) {
				obj.put("status",false);
				obj.put("message", "Invalid Type");
				obj.put("data", "");
			}
			else if(model == null) {
				obj.put("status",false);
				obj.put("message", "Null model");
				obj.put("data", "");
			}
			else if(model.length() > 20){
				obj.put("status",false);
				obj.put("message", "Model name too long");
				obj.put("data", "");
			}
			else if(color == null) {
				obj.put("status",false);
				obj.put("message", "Null color");
				obj.put("data", "");
			}
			else if(color.length() > 20){
				obj.put("status",false);
				obj.put("message", "Color name too long");
				obj.put("data", "");
			}
			else {
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String rel_ride = "insert into ride values(?,?::ride_,?,?,'user'::ownedby_, 'with_owner'::status_, null, null)";
				String rel_own = "insert into ownership values(?,?)";
				String rel_ride_seq = "insert into ride_seq(txt) values('1')";

				String rideid = getNewRideID();
				System.out.println("New rideid: "+rideid);
				PreparedStatement stmt1 = conn.prepareStatement(rel_ride);
				stmt1.setString(1, rideid);
				stmt1.setString(2, type);
				stmt1.setString(3, model);
				stmt1.setString(4, color);

				System.out.println("chkpt 2");

				PreparedStatement stmt2 = conn.prepareStatement(rel_own);
				stmt2.setString(1, rideid);
				stmt2.setString(2, userid);

				System.out.println("chkpt 3");

				PreparedStatement stmt3 = conn.prepareStatement(rel_ride_seq);

				try {
					System.out.println("chkpt 4:"+stmt1.toString());
					conn.setAutoCommit(false);
					if(stmt1.executeUpdate() == 0) {
						obj.put("status", false);
						obj.put("message", "Bike already exists!");
						obj.put("data", "");
						conn.rollback();
						conn.setAutoCommit(true);
						stmt1.close();
						conn.close();

						System.out.println(obj);
						return obj;
					}
					else {
						System.out.println("chkpt 5:"+stmt2.toString());
						stmt2.executeUpdate();
						System.out.println("chkpt 5.1:"+stmt3.toString());
						stmt3.executeUpdate();
						conn.commit();

						conn.setAutoCommit(true);
						stmt1.close();
						stmt2.close();
						stmt3.close();
						conn.close();

						obj.put("status", true);
						obj.put("message", "");
						obj.put("data", "");

						System.out.println(obj);
						return obj;
					}
				}
				catch(SQLException se) {
					System.out.println("chkpt 6");
					System.out.println(se);
					conn.rollback();
					conn.setAutoCommit(true);
				}
				System.out.println("chkpt 7");
				stmt1.close();
				stmt2.close();
				stmt3.close();
				conn.close();

				obj.put("status", false);
				obj.put("message", "SQLException occured");
				obj.put("data", "");
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static String getNewRideID() {
		String id = null;

		Connection conn;
		try {
			conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select concat('RD',lpad((max(id))::text,3,'0')) from ride_seq";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			if(result.next()) {
				id = result.getString(1);
			}
			else {
				id = "000";
			}
			preparedStmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public static JSONObject Lend(String userid, String rideid, String optype) {
		JSONObject obj = new JSONObject();
		System.out.println("Lend: "+userid+","+rideid+","+optype);

		try {
			if (userid == null){
				obj.put("status", false);
				obj.put("message", BAD_USER);
				System.out.println(obj);
				return obj;
			}

			if (optype.equals("Place")){
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select count(*) from ownership natural join ride where ownership.ownerid=? and ride.rideid=? and ride.status='with_owner'::status_";
				String upd = "insert into requests values (?,?,?::request_,null)";

				PreparedStatement stmt1 = conn.prepareStatement(query);
				stmt1.setString(1, userid);
				stmt1.setString(2, rideid);

				PreparedStatement stmt2 = conn.prepareStatement(upd);
				stmt2.setString(1, rideid);
				stmt2.setString(2, userid);
				stmt2.setString(3, "lend");

				conn.setAutoCommit(false);
				ResultSet result = stmt1.executeQuery();
				if(!result.isBeforeFirst() ) {
					obj.put("status", false);
					obj.put("message", "Illegal request!");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}
				if (stmt2.executeUpdate() < 1){
					obj.put("status", false);
					obj.put("message", "Error placing request. Please try again later");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}
				conn.commit();

				obj.put("status", true);
				obj.put("message", "");
				obj.put("data", 1);
			}
			else if (optype.equals("Cancel")){
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select * from requests where rideid=? and type=('lend'::request_)";
				String upd = "delete from requests where rideid=?";

				PreparedStatement stmt1 = conn.prepareStatement(query);
				stmt1.setString(1, rideid);

				PreparedStatement stmt2 = conn.prepareStatement(upd);
				stmt2.setString(1, rideid);
//				stmt2.setString(2, userid);
//				stmt2.setString(3, "lend");

				System.out.println(stmt1.toString());

				conn.setAutoCommit(false);
				ResultSet result = stmt1.executeQuery();
				if(!result.isBeforeFirst() ) {
//					obj.put("status", false);
//					obj.put("message", "Illegal request!");
//					conn.rollback();
//					conn.setAutoCommit(true);
//					conn.close();
//					System.out.println(obj);
//					return obj;
				}
				
				System.out.println(stmt2.toString());
				if (stmt2.executeUpdate() < 1){
					obj.put("status", false);
					obj.put("message", "Error placing request. Please try again later");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}
				conn.commit();
				obj.put("status", true);
				obj.put("message", "");
				obj.put("data", 0);
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}

	public static JSONObject Unlend(String userid, String rideid, String optype) {
		JSONObject obj = new JSONObject();
		System.out.println("Lend: "+userid+","+rideid+","+optype);

		try{
			if (userid == null){
				obj.put("status", false);
				obj.put("message", BAD_USER);
				System.out.println(obj);
				return obj;
			}

			if (optype.equals("Place")){
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select count(*) from ownership natural join ride where ownership.ownerid=? and ride.rideid=? and ride.status=('lent'::status_)";
				String upd = "insert into requests values (?,?,?::request_,null)";

				PreparedStatement stmt1 = conn.prepareStatement(query);
				stmt1.setString(1, userid);
				stmt1.setString(2, rideid);

				PreparedStatement stmt2 = conn.prepareStatement(upd);
				stmt2.setString(1, rideid);
				stmt2.setString(2, userid);
				stmt2.setString(3, "unlend");

				System.out.println(stmt1.toString());
				conn.setAutoCommit(false);
				ResultSet result = stmt1.executeQuery();
				if(!result.isBeforeFirst() ) {
					obj.put("status", false);
					obj.put("message", "Illegal request!");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}

				System.out.println(stmt2.toString());
				if (stmt2.executeUpdate() < 1){
					obj.put("status", false);
					obj.put("message", "Error placing request. Please try again later");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}
				conn.commit();
				obj.put("status", true);
				obj.put("message", "");
				obj.put("data", 3);
			}
			else if (optype.equals("Cancel")){
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select * from requests where rideid=? and type=('unlend'::request_)";
				String upd = "delete from requests where rideid=?";

				PreparedStatement stmt1 = conn.prepareStatement(query);
				stmt1.setString(1, rideid);

				PreparedStatement stmt2 = conn.prepareStatement(upd);
				stmt2.setString(1, rideid);
//				stmt2.setString(2, userid);
//				stmt2.setString(3, "lend");

				System.out.println(stmt1.toString());
				conn.setAutoCommit(false);
				ResultSet result = stmt1.executeQuery();
				if(!result.isBeforeFirst() ) {
					obj.put("status", false);
					obj.put("message", "Illegal request!");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}
				System.out.println(stmt2.toString());
				if (stmt2.executeUpdate() < 1){
					obj.put("status", false);
					obj.put("message", "Error placing request. Please try again later");
					conn.rollback();
					conn.setAutoCommit(true);
					conn.close();
					System.out.println(obj);
					return obj;
				}
				conn.commit();
				obj.put("status", true);
				obj.put("message", "");
				obj.put("data", 0);
			}
		}
		catch(Exception e) {
			System.out.println(e);
			try{
				obj.put("status",false);
				obj.put("message",e);
				obj.put("data", "");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
		}
		System.out.println(obj);
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
				System.out.println(preparedStmt.toString());
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
	
	public static String bike_present_at_stand(String bikeID){
		String standid = null;
		System.out.println("authenticate:"+bikeID);
		try{
			if(bikeID == null) {
				return null;
			}
			else {
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "select standid from ride_at_stand where rideID=?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, bikeID);
				ResultSet result =  preparedStmt.executeQuery();
				if(result.next())
					standid = result.getString(1);
				
				preparedStmt.close();
				conn.close();
				return standid;
			}			
		} 
		catch(Exception e) {
			System.out.println(e);
		}
		return standid;
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
				stmt.setString(3, password);
				
				try{
					if (stmt.executeUpdate() == 0) {
						obj.put("status", false);
						obj.put("message", "Email already registered");
						conn.close();
						System.out.println(obj);
						return obj;
					} else {
						JSONObject temp = new JSONObject();
						temp.put("email", id);
						temp.put("name", name);
						obj.put("status", true);
						obj.put("message", "");
						obj.put("data", temp);

						HttpSession session = request.getSession(true);
						session.setAttribute(DbHandler.USER_ATTR, id);
					}
				}
				catch(JSONException e1){
					obj.put("status", false);
					obj.put("message", "Duplicate User");
					conn.close();
					System.out.println(obj);
					return obj;
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
		System.out.println(obj);
		return obj;
	}

	public static JSONObject setProfile(String id, String name, String address, String phone){
		JSONObject obj = new JSONObject();
		try{
			if(id == null) {
				obj.put("status",false);
				obj.put("message", BAD_USER);
			}
			else if(name == null) {
				obj.put("status",false);
				obj.put("message", "Null name");
			}
			else if(name.length()>40) {
				obj.put("status",false);
				obj.put("message", "Name too long");
			}
			else if(address.length()>60) {
				obj.put("status",false);
				obj.put("message", "Address too long");
			}
			else if(phone.length()>20) {
				obj.put("status",false);
				obj.put("message", "Phone Number too long");
			}
			else {
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				String query = "update rider set name=?,address=?,phone=? where riderid=?";

				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1, name);
				stmt.setString(2, address);
				stmt.setString(3, phone);
				stmt.setString(4, id);
				if(stmt.executeUpdate() == 0) {
					obj.put("status", false);
					obj.put("message", "Error while updating");
					conn.close();
					System.out.println(obj);
					return obj;
				}
				else {
					obj.put("status", true);
					obj.put("message", "");
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
		System.out.println(obj);
		return obj;
	}

	public static JSONObject getProfile(String id){
		JSONObject obj = new JSONObject();
		System.out.println("getProfile: "+id);

		if(id == null) {
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

		String ret_cols = "riderid,name,address,phone";
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query =  "select "+ret_cols+" from rider where riderid=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			ResultSet result =  preparedStmt.executeQuery();

			JSONArray stnds = ResultSetConverter(result);
			preparedStmt.close();
			conn.close();

			if(stnds.length()<1) {
				obj.put("status",false);
				obj.put("message","Rider not found");
			}
			else {
				obj.put("status",true);
				obj.put("data", stnds.get(0));
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
		System.out.println(obj);
		return obj;
	}
	
	public static JSONObject getUserData(HttpServletRequest request){
		JSONObject obj = new JSONObject();
		System.out.println("getUserData: ");

		HttpSession session = request.getSession();
		
		if(session == null) {
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
		String id = (String) session.getAttribute(USER_ATTR);
		if(id == null) {
			try{
				obj.put("status",false);
				obj.put("message",BAD_USER + ": id not present");
			}
			catch(JSONException e1){
				System.out.println(e1);
			}
			System.out.println(obj);
			return obj;
		}
		
		String ret_cols = "riderid,name";
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query =  "select "+ret_cols+" from rider where riderid=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			ResultSet result =  preparedStmt.executeQuery();

			JSONArray stnds = ResultSetConverter(result);
			preparedStmt.close();
			conn.close();

			if(stnds.length()<1) {
				obj.put("status",false);
				obj.put("message","Rider not found");
			}
			else {
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
		System.out.println(obj);
		return obj;
	}

	public static boolean admin_auth(HttpServletRequest request, String id, String pass){
		System.out.println("admin_auth: " + id + "," + pass);
		
		if (!id.equals(admin) || !pass.equals(admin_pass)){
			return false;
		}
		
		HttpSession session = request.getSession(true);
		System.out.println("here");
		session.setAttribute(IS_ADMIN, true);
		System.out.println("here");
		if(session.isNew()){
			System.out.println("New Session for admin");
		}
		else{
			System.out.println("Using old Session for admin");
		}
		return true;
	}

	public static JSONObject getRequests(HttpServletRequest request) throws JSONException{		
		JSONObject obj = new JSONObject();
		String query = "select * from requests";

		System.out.println("getRequests: " + request.getSession().getAttribute(IS_ADMIN));
		
		if (request.getSession().getAttribute(IS_ADMIN) == null || !(Boolean)request.getSession().getAttribute(IS_ADMIN)){
			obj.put("status", false);				
			obj.put("message", BAD_USER);
			System.out.println(obj);
			return obj;
		}
		try {
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet result = preparedStmt.executeQuery();
			JSONArray stnds = ResultSetConverter(result);
			preparedStmt.close();
			conn.close();

			obj.put("status", true);
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

	//handle all corner cases and other details later on
	public static boolean handleLend(HttpServletRequest request, String rideid, String type) throws SQLException{
		System.out.println("handleLend "+ rideid+", "+ type);
		if (request.getSession().getAttribute(IS_ADMIN) == null || (!type.equals("accept") && !type.equals("reject"))){
			System.out.println("HandleLend: Invalid input" + " , " + type);
			// obj.put("status", false);				
			// obj.put("message", BAD_USER);
			return false;
		}
		if(type.equals("accept")){
			//for now insert ride always at the first stand - 's1'
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			try {
				conn.setAutoCommit(false);
				
				String upd1 = "delete from requests where rideid=?";
				String upd2 = "insert into ride_at_stand values (?, 's1')"; 
				String upd3 = "update ride set status='lent'::status_ where rideid=?";
				
				PreparedStatement preparedStmt1 = conn.prepareStatement(upd1);
				preparedStmt1.setString(1, rideid);
				PreparedStatement preparedStmt2 = conn.prepareStatement(upd2);
				preparedStmt2.setString(1, rideid);
				PreparedStatement preparedStmt3 = conn.prepareStatement(upd3);
				preparedStmt3.setString(1, rideid);

				System.out.println(preparedStmt1.toString());
				System.out.println(preparedStmt2.toString());
				System.out.println(preparedStmt3.toString());
				
				if (preparedStmt1.executeUpdate() < 1 ){
					System.out.println("HandleLend: Checkpoint1");
					preparedStmt1.close();
					conn.close();
					return false;
				}
				if(preparedStmt2.executeUpdate() == 0) {
					System.out.println("HandleLend: Checkpoint2");
					conn.rollback();
					conn.setAutoCommit(true);
					preparedStmt1.close();
					preparedStmt2.close();
					conn.close();
					return false;
				}
				if(preparedStmt3.executeUpdate() == 0) {
					System.out.println("HandleLend: Checkpoint3");
					conn.rollback();
					conn.setAutoCommit(true);
					preparedStmt1.close();
					preparedStmt2.close();
					preparedStmt3.close();
					conn.close();
					return false;
				}
				System.out.println("finishing");
				preparedStmt1.close();
				preparedStmt2.close();
				preparedStmt3.close();
				conn.commit();
				conn.setAutoCommit(true);
				conn.close();
				return true;
			}
			catch(SQLException se) {
				System.out.println(se);
				conn.rollback();
				conn.setAutoCommit(true);
				conn.close();
				return false;
			}
			
			
		}
		
		else if (type.equals("reject")) {
			System.out.println("Handle Lend Reject: " + rideid);
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "delete from requests where rideid=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, rideid);
			System.out.println(preparedStmt.toString());
			if (preparedStmt.executeUpdate() < 1 ){
				System.out.println("HandleLend: Checkpoint4");				
				preparedStmt.close();
				conn.close();
				return false;
			}
			preparedStmt.close();
			conn.close();
			return true;
		}
		
		System.out.println("HandleLend: Checkpoint5");
		return false;
	}

	//handle all corner cases and other details later on
	public static boolean handleUnlend(HttpServletRequest request, String rideid, String type) throws SQLException{
		System.out.println("handleUnlend: " + request.getSession().getAttribute(IS_ADMIN)+","+"type");
		if (request.getSession().getAttribute(IS_ADMIN) == null || (!type.equals("accept") && !type.equals("reject"))){
			// obj.put("status", false);				
			// obj.put("message", BAD_USER);
			return false;
		}
		System.out.println("Handle Unlend Accept");
		if(type.equals("accept")){
			//for now insert ride always at the first stand - 's1'
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			try {
				System.out.println("Handle Unlend Accept");
				conn.setAutoCommit(false);
				
				String upd1 = "delete from requests where rideid=?;";
				String upd2 = "delete from ride_at_stand where rideid=?;";
				String upd3 = "update ride set status='with_owner'::status_ where rideid=?";
				
				PreparedStatement preparedStmt1 = conn.prepareStatement(upd1);
				preparedStmt1.setString(1, rideid);
				PreparedStatement preparedStmt2 = conn.prepareStatement(upd2);
				preparedStmt2.setString(1, rideid);
				PreparedStatement preparedStmt3 = conn.prepareStatement(upd3);
				preparedStmt3.setString(1, rideid);
				
				System.out.println("===============");
				System.out.println(preparedStmt1.toString());
				System.out.println(preparedStmt2.toString());
				System.out.println(preparedStmt3.toString());
				System.out.println("===============");
				if (preparedStmt1.executeUpdate() < 1 ){
					System.out.println("here1");
					preparedStmt1.close();
					conn.close();
					return false;
				}
				if(preparedStmt2.executeUpdate() == 0) {
					System.out.println("here2");
					conn.rollback();
					conn.setAutoCommit(true);
					preparedStmt1.close();
					preparedStmt2.close();
					conn.close();
					return false;
				}
				preparedStmt3.executeUpdate();
				preparedStmt1.close();
				preparedStmt2.close();
				preparedStmt3.close();
				conn.commit();
				conn.setAutoCommit(true);
				conn.close();
				return true;
			}
			catch(SQLException se) {
				conn.rollback();
				conn.setAutoCommit(true);
				conn.close();
				return false;
			}
		}
		
		else if (type.equals("reject")) {
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "delete from requests where rideid=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, rideid);
			if (preparedStmt.executeUpdate() < 1 ){
				System.out.println("here4");
				preparedStmt.close();
				conn.close();
				return false;
			}
			preparedStmt.close();
			conn.close();
			return true;
		}
		
		System.out.println("here5");
		return false;
	}
	
	public static JSONObject getHistory(String userID) {
		JSONObject obj = new JSONObject();
		System.out.println("getHistory: "+userID);
		
		String query = "select makemodel as model, color, sfr.name as fromstand, fromtime, sto.name as tostand, totime "
				+"from (history natural join ride natural join stand sfr) left outer join stand sto "
				+"on sto.standid = tostandid "
				+"where sfr.standid = fromstandid "
				+"and userid = ? ";

		try {
			if (userID == null){
					obj.put("status", false);
					obj.put("message", BAD_USER);
					obj.put("data", "");
			}
			else{
				// Create the connection
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, userID);

				//System.out.println(preparedStmt.toString());

				ResultSet result = preparedStmt.executeQuery();
				JSONArray stnds = ResultSetConverter(result);
				preparedStmt.close();
				conn.close();

				obj.put("status", true);
				obj.put("message", "");
				obj.put("data", stnds);
			}
		} catch (Exception e) {
			System.out.println(e);
			try {
				obj.put("status", false);
				obj.put("message", e);
				obj.put("data", "");
			} catch (JSONException e1) {
				System.out.println(e1);
			}
		}
		System.out.println(obj);
		return obj;
	}
	
	

}