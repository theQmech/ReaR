
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
@WebServlet("/LendUnlend")
public class LendUnlend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LendUnlend() {
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
		String op = request.getParameter("op");
		String optype = request.getParameter("optype");
		
		System.out.println("LendUnlend: "+userID+", "+rideid+", "+op+", "+optype);
		
		JSONObject obj = new JSONObject();
		
		boolean valid = (userID!=null && rideid!=null && op!=null && optype!=null 
				&& (op.equals("Lend") || op.equals("Unlend")) && (optype.equals("Cancel") || optype.equals("Place")));
		
		if (!valid){
			try {
				obj.put("status", false);
				obj.put("message", "Incomplete request!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else{
			if (op.equals("Lend")){
				obj = DbHandler.Lend(userID, rideid, optype);
			}
			else if (op.equals("Unlend")){
				obj = DbHandler.Unlend(userID, rideid, optype);
			}
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

}