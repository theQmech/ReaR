package main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Stands
 */
@WebServlet("/Stands")
public class Stands extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Stands() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = null;
		HttpSession session = request.getSession(false);
		if(session != null) {
			id = (String) session.getAttribute(DbHandler.USER_ATTR);
			System.out.println("Stands:: got "+DbHandler.USER_ATTR+": "+id);
			System.out.println(request.toString());
		}
		else {
			System.out.println("Stands: Session not set");
		}

		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");		
		PrintWriter out = response.getWriter();		
		out.print(DbHandler.getStands(id));
		out.close();
	}
}