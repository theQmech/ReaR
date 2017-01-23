
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class BikeList
 */
@WebServlet("/RidesAtStand")
public class RidesAtStand extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RidesAtStand() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String userID = null;
		if (session != null){
			userID = (String) session.getAttribute(DbHandler.USER_ATTR);
		}
		String standID = request.getParameter("standid");
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();		
		out.print(DbHandler.getRidesAtStand(userID,standID));
		out.close();
	}

}