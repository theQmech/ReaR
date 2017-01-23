
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Requests
 */
@WebServlet("/Requests")
public class Requests extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Requests() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		try {
			obj = DbHandler.getRequests(request);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Requests Servlet: " + obj.toString());
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}

//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		doGet(request, response);
//	}

}
