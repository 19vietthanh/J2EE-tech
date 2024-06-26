package Servlet;

import Data.User;
import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.net.*;
import java.util.Scanner;

/**
 *
 * @author Viet Thanh
 */

public class Update extends HttpServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/db_quanlymaytinh";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String IP = request.getParameter("IP");
        String HDH = request.getParameter("HeDieuHanh");
        String Role = request.getParameter("Role");
        String tt_ram = request.getParameter("RAM");
        String tt_rom = request.getParameter("ROM");

        HttpSession session = request.getSession();
        User user;
        user = (User) session.getAttribute("user");
        String ND = user.getUsername();

        // Ping to the IP address
        boolean isReachable = isReachable(IP);

        // Check if computer exists in the database
        boolean computerExists = false;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD); PreparedStatement statement = connection.prepareStatement("SELECT * FROM tt_computer WHERE IP = ?")) {
            statement.setString(1, IP);
            ResultSet resultSet = statement.executeQuery();
            computerExists = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<p>Lỗi khi truy vấn database!</p>");
        }

        // Only allow editing if the computer exists
        if (computerExists) {
            // Update the existing record
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
                PreparedStatement statement = connection.prepareStatement("UPDATE tt_computer SET HeDieuHanh = ?, VaiTro = ?, in4_Ram = ?, in4_Rom = ? WHERE IP = ?")) {
                statement.setString(1, HDH);
                statement.setString(2, Role);
                statement.setString(3, tt_ram);
                statement.setString(4, tt_rom);
                statement.setString(5, IP);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Computer information updated successfully!");
                } else {
                    System.out.println("No computer found with the provided IP address!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().println("<p>Lỗi khi cập nhật dữ liệu vào database!</p>");
            }
        } else {
            // Display an error message
            response.getWriter().println("<p>Máy tính không tồn tại!</p>");
        }

        // Set response content type
        response.setContentType("text/html;charset=UTF-8");

        // Write response
        try (PrintWriter out = response.getWriter()) {
            if (isReachable) {
                String status = "Online";
                if (computerExists) {
                    out.println("<p>Thông tin máy tính đã được cập nhật!</p>");
                } else {
                    out.println("<p>Máy tính không tồn tại!</p>");
                }
            } else {
                out.println("<p>Máy tính không khả dụng!</p>");
            }
            out.println("</body>");
            out.println("</html>");
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Đã cập nhật thông tin thành công!');");
            out.println("window.location.href = 'index.jsp';");
            out.println("</script>");
        }
    }

    private boolean isReachable(String IP) {
        try {
            // Replace "1000" with your preferred timeout in milliseconds
            return InetAddress.getByName(IP).isReachable(1000);
        } catch (IOException e) {
            return false;
        }
    }

}
