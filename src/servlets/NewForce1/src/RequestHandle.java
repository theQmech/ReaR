

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
 * Servlet implementation class RequestHandle
 */
@WebServlet("/RequestHandle")
public class RequestHandle extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RequestHandle() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String rideid = request.getParameter("rideid");
		String op = request.getParameter("op");
		String type = request.getParameter("type");
		
		System.out.println("RequestHandle:" + rideid +","+op+","+type);
		
		boolean status = false;
		if (op.equals("lend")){
			System.out.println("here1");
			try {
				status = DbHandler.handleLend(request, rideid, type);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (op.equals("unlend")){
			try {
				System.out.println("here2");
				status = DbHandler.handleUnlend(request, rideid, type);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		JSONObject obj = new JSONObject();
		try {
			obj = DbHandler.getRequests(request);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			obj.put("status", status);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("RequestHandle: " + status);
		System.out.println("Requests Servlet: " + obj.toString());
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.sendRedirect("reqs.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
