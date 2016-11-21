

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Profile
 */
@WebServlet("/Profile")
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id = null;
		HttpSession session = request.getSession(false);
		if(session != null) {
			id = (String) session.getAttribute(DbHandler.USER_ATTR);
		}
		else {
			System.out.println("Profile get: Session not set");
		}
		System.out.println("Profile get: "+id);

		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();		
		out.print(DbHandler.getProfile(id));
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id = null;
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		HttpSession session = request.getSession(false);
		if(session != null) {
			id = (String) session.getAttribute(DbHandler.USER_ATTR);
		}
		else {
			System.out.println("Profile Post: Session not set");
		}
		System.out.println("Profile post: "+id+","+name+","+address+","+phone);
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();		
		out.print(DbHandler.setProfile(id, name, address, phone));
		out.close();
	}

}
