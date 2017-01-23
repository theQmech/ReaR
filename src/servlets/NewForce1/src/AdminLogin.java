
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AdminLogin
 */
@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		if (session != null){
			Object obj = session.getAttribute(DbHandler.IS_ADMIN);
			if (obj!=null){
				if ((boolean)obj){
					response.sendRedirect("reqs.jsp");
					return;
				}
			}
		}
		request.getRequestDispatcher("/adminlogin.jsp").forward(request, response);
	}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("here");
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (DbHandler.admin_auth(request, id, password)){
			System.out.println("Admin: "+id+","+password);
//			RequestDispatcher rd = request.getRequestDispatcher("/Requests");
//		    rd.forward(request, response);
			response.sendRedirect("reqs.jsp");
		}
		else{
			out.print("Invalid Login");
		}
		out.close();
	}

}
