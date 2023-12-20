package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    public void doGet(HttpServletRequest rec, HttpServletResponse res) throws IOException, ServletException {
        HttpSession session = rec.getSession();
        Field field = extractField(session);
        int index = getIndex(rec);
        Map<Integer, Sign> cellsField = field.getField();
        if (cellsField.get(index) != Sign.EMPTY) {
            getServletContext().getRequestDispatcher("index.jsp").forward(rec, res);
            return;
        }
        cellsField.put(index, Sign.CROSS);
        if (checkWin(res, session, field)) {
            return;
        }
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            cellsField.put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(res, session, field)) {
                return;
            }
        }
        List<Sign> data = field.getFieldData();
        session.setAttribute("field", field);
        session.setAttribute("data", data);
        res.sendRedirect("index.jsp");
    }

    public Field extractField(HttpSession session) {
        Object fieldAttribute = session.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken");
        }
        return (Field) fieldAttribute;
    }

    private int getIndex(HttpServletRequest rec) {
        String param = rec.getParameter("click");
        boolean isDigit = param.chars().allMatch(Character::isDigit);
        return isDigit ? Integer.parseInt(param) : 0;
    }

    private boolean checkWin(HttpServletResponse res, HttpSession session, Field field) throws IOException {
        Sign signWin = field.checkWin();
        if (signWin == Sign.NOUGHT || signWin == Sign.CROSS) {
            session.setAttribute("winner", signWin);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            res.sendRedirect("/index.jsp");
            return true;
        } else
            return false;
    }
}

