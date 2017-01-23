

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("LogoutServlet: ");
		JSONObject jobj = new JSONObject();	
		HttpSession session = request.getSession();
			if (session != null){
				Object obj = session.getAttribute(DbHandler.USER_ATTR);
				if (obj!=null){
					session.invalidate();
					try {
						jobj = new JSONObject().put("status", true).put("message", "").put("data", "");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PrintWriter out = response.getWriter();
					out.print(jobj);
					out.close();
					System.out.println("LogoutServlet: Logging out "+obj.toString()+ "  " + jobj.toString());
					return;
				}
				else{
					session.invalidate();
					try {
						jobj = new JSONObject().put("status", false).put("message", "User Attributes were not set").put("data", "");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PrintWriter out = response.getWriter();
					out.print(jobj);
					out.close();
					return;
				}
			}
			else try {
				jobj = new JSONObject().put("status", DbHandler.BAD_USER).put("message", "").put("data", "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PrintWriter out = response.getWriter();
			out.print(jobj);
			out.close();
			response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
