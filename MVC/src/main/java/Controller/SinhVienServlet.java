package Controller;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import Model.bo.SinhVienBO;
import Model.bean.SinhVien;

@WebServlet("/SinhVienServlet")
public class SinhVienServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SinhVienBO sinhVienBO = new SinhVienBO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isAuthenticated(request, response)) {
			return;
		}

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		if (action == null) action = "list";

		switch (action) {
			case "list":
				ArrayList<SinhVien> dsSV = sinhVienBO.getAllSinhVien();
				request.setAttribute("dsSV", dsSV);
				request.getRequestDispatcher("/SinhVienView/SinhVienList.jsp").forward(request, response);
				break;

			case "addSV":
				request.getRequestDispatcher("/SinhVienView/InsertSinhVien.jsp").forward(request, response);
				break;

			case "editSV":
				String maSv = request.getParameter("id");
				if (maSv == null) {
					request.setAttribute("dsSV", sinhVienBO.getAllSinhVien());
					request.getRequestDispatcher("/SinhVienView/UpdateSVList.jsp")
					.forward(request, response);
				} else {
					SinhVien sv = sinhVienBO.getSinhVienByID(maSv);
					request.setAttribute("sinhVien", sv);
					request.getRequestDispatcher("/SinhVienView/FormUpdateSV.jsp").forward(request, response);
				}
				break;

			case "deleteSV":
				String idDelete = request.getParameter("id");
				if (idDelete != null) {
					sinhVienBO.deleteSinhVien(idDelete);
					request.setAttribute("msg", "Đã xoá sinh viên [" + idDelete + "] thành công");
				}
				request.setAttribute("dsSV", sinhVienBO.getAllSinhVien());
				request.getRequestDispatcher("/SinhVienView/DeleteSVList.jsp").forward(request, response);
				break;

			case "deleteAll":
				request.setAttribute("dsSV", sinhVienBO.getAllSinhVien());
				request.getRequestDispatcher("/SinhVienView/DeleteAllSV.jsp").forward(request, response);
				break;

			default:
				response.sendRedirect(request.getContextPath() + "/SinhVienServlet?action=list");
				break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isAuthenticated(request, response)) {
			return;
		}

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		if (action == null) action = "";

		switch (action) {
			case "insert":
				String maSv = request.getParameter("maSv");
				String hoTen = request.getParameter("hoTen");
				String lop = request.getParameter("lop");
				float diemTb = parseDiemTb(request.getParameter("diemTb"));

				SinhVien svInsert = new SinhVien(maSv, hoTen, lop, diemTb);
				sinhVienBO.insertSinhVien(svInsert);
				response.sendRedirect(request.getContextPath() + "/SinhVienServlet?action=list");
				break;

			case "update":
				String idUpdate = request.getParameter("maSv");
				String tenUpdate = request.getParameter("hoTen");
				String lopUpdate = request.getParameter("lop");
				float diemTbUpdate = parseDiemTb(request.getParameter("diemTb"));

				SinhVien svUpdate = new SinhVien(idUpdate, tenUpdate, lopUpdate, diemTbUpdate);
				sinhVienBO.updateSinhVien(svUpdate);
				response.sendRedirect(request.getContextPath() + "/SinhVienServlet?action=list");
				break;

			case "deleteAll":
				String[] ids = request.getParameterValues("maSv");
				ArrayList<String> listMaSv = (ids != null) ? new ArrayList<>(Arrays.asList(ids)) : new ArrayList<>();

				int count = sinhVienBO.deleteAllSinhVien(listMaSv);
				String msg = (count > 0)
						? "Đã xóa thành công " + count + " sinh viên."
						: "Không có sinh viên nào được chọn.";

				request.setAttribute("msg", msg);
				request.setAttribute("dsSV", sinhVienBO.getAllSinhVien());
				request.getRequestDispatcher("/SinhVienView/DeleteAllSV.jsp").forward(request, response);
				break;

			default:
				response.sendRedirect(request.getContextPath() + "/SinhVienServlet?action=list");
				break;
		}
	}

	private boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return false;
		}
		return true;
	}

	private float parseDiemTb(String diemTbRaw) {
		try {
			return Float.parseFloat(diemTbRaw);
		} catch (NumberFormatException ex) {
			return 0f;
		}
	}
}
