package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class RentOp
 */
@WebServlet("/RentOp")
public class RentOp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RentOp() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userid = request.getParameter("UserID");
		String bikeid = request.getParameter("BikeID");
		String standid = request.getParameter("StandID");
		String op = request.getParameter("Op");
		
		System.out.println("RentOp: "+userid+", "+bikeid+", "+standid+", "+op);
		
		JSONObject obj = new JSONObject();
		if (userid != null && bikeid!=null && standid!=null && (op.equals("rent")|| op.equals("unrent"))){
			if (op.equals("rent")){
				try {
					obj = DbHandler.Rent(userid, bikeid, standid);
				} catch (JSONException | SQLException e) {
					e.printStackTrace();
				}
			}
			else if (op.equals("unrent")){
				try {
					obj = DbHandler.Unrent(userid, bikeid, standid);
				} catch (JSONException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			try {
				obj.put("status",false);
				obj.put("message", "Invalid Data");	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

}