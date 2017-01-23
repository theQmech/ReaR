

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class History
 */
@WebServlet("/History")
public class History extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public History() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		String userID = null;
		if (session != null){
			userID = (String) session.getAttribute(DbHandler.USER_ATTR);
		}
		System.out.println("History: "+userID);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();		
		out.print(DbHandler.getHistory(userID));
		out.close();

	}

}
