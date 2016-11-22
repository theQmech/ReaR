
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class RentOp
 */
@WebServlet("/RentUnrent")
public class RentUnrent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RentUnrent() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String userID = null;
		if (session != null){
			userID = (String) session.getAttribute(DbHandler.USER_ATTR);
		}
		String rideid = request.getParameter("rideid");
		String standid = request.getParameter("standid");
		String op = request.getParameter("op");
		
		System.out.println("RentOp: "+userID+", "+rideid+", "+standid+", "+op);
		
		JSONObject obj = new JSONObject();
		if (userID != null && rideid!=null && standid!=null && (op.equals("rent")|| op.equals("unrent"))){
			if (op.equals("rent")){
				obj = DbHandler.Rent(userID, rideid, standid);
			}
			else if (op.equals("unrent")){
				obj = DbHandler.Unrent(userID, rideid, standid);
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