package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import dbmanager.GetTweetsRequest;

/**
 * Servlet implementation class Tweets
 */
public class Tweets extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tweets() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String keyword = request.getParameter("keyword");
        String language = request.getParameter("language");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        response.setContentType("application/json");    
        try {
            GetTweetsRequest getTweetsRequest = new GetTweetsRequest(keyword, language, startDate, endDate);
            JSONObject result = new JSONObject();
            result.put("coordinates", getTweetsRequest.getCoordinates());
            result.put("status", 200);
            response.getOutputStream().print(result.toString());
            getTweetsRequest.shutdown();
        } catch (JSONException | ProvisionedThroughputExceededException e) {
            response.getOutputStream().print("{\"status\":400,\"error\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
    
}
