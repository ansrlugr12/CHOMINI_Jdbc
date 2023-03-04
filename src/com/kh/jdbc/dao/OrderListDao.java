package com.kh.jdbc.dao;
import com.kh.jdbc.util.Common;
import com.kh.jdbc.vo.CusVO;
import com.kh.jdbc.vo.OrderListVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderListDao {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    Scanner sc = new Scanner(System.in);
    public List<OrderListVO> orderListSelect() {
        List<OrderListVO> orderListVOList = new ArrayList<>();
        try{
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM ORDER_LIST";
            rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int orderNo = rs.getInt("주문번호");
                Date date = rs.getDate("주문날짜");
                int customNo = rs.getInt("회원번호");
                String menuName = rs.getString("메뉴이름");
                String size = rs.getString("사이즈");
                String bread = rs.getString("빵");
                String cheese = rs.getString("치즈");
                String vegetable = rs.getString("야채");
                String sauce = rs.getString("소스");
                int howMany = rs.getInt("수량");
                int total = rs.getInt("합계");
                OrderListVO orderListVO =
                        new OrderListVO(orderNo, date, customNo, menuName, size, bread, cheese, vegetable, sauce, howMany, total);
                orderListVOList.add(orderListVO);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderListVOList;
    }


    // 그냥 리스트 프린트 !
    public void orderSelectPrint(List<OrderListVO> orderListVOList) {
        for (OrderListVO o : orderListVOList) {
            System.out.println("--------- 주문 내역을 확인하세요 ---------");
            System.out.println("주문 번호 : " + o.getOrderNo());
            System.out.println("주문 날짜 : " + o.getDate());
            System.out.println("회원 번호 : " + o.getCustomNo());
            System.out.println("메뉴 이름 : " + o.getMenuName());
            System.out.println("사 이 즈 : " + o.getSize());
            System.out.println("빵       : " + o.getBread());
            System.out.println("치즈     : " + o.getCheese());
            System.out.println("야채     : " + o.getVegetable());
            System.out.println("소스     : " + o.getSauce());
            System.out.println("수    량 : " + o.getHowMany());
            System.out.println("합   계  : " + o.getTotal());
            System.out.println("--------------------------------------");
        }
    }


    public void dailySales() {
        System.out.print("조회할 날짜를 입력하세요(yy/mm/dd) : ");
        String day = sc.next();

        String sql = "SELECT * FROM ORDER_LIST WHERE TO_DATE(주문날짜, 'YY/MM/DD') = TO_DATE(?, 'YY/MM/DD') ";

        try {
            conn = Common.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, day);
            pStmt.executeUpdate();
            rs = pStmt.getResultSet();

            int total = 0;
            System.out.println();
            System.out.println("================== [" + day + " 주문내역] ==================");
            System.out.println("  ♡날  짜♡    ♡메 뉴♡   ♡사이즈♡  ♡수량♡   ♡합계♡");
            while (rs.next()) {
                System.out.print(rs.getDate("주문날짜") );
                System.out.printf("%10s ", rs.getString("메뉴이름"));
                System.out.printf("%4s", rs.getString("사이즈"));
                System.out.printf("%9d", rs.getInt("수량"));
                System.out.printf("%11d", rs.getInt("합계"));
                total += rs.getInt("합계");
                System.out.println();
            }
            System.out.println("해당 날짜의 총 매출 금액은 " + total + "원 입니다.");
            System.out.println("=====================================================");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pStmt);
        Common.close(conn);

    }


    public void menuCount() {

        String sql = "SELECT 메뉴이름, COUNT(메뉴이름) AS 판매수량\n" +
                "    FROM ORDER_LIST\n" +
                "    GROUP BY 메뉴이름" ;
        try {
            conn = Common.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.executeUpdate();
            rs = pStmt.getResultSet();
            System.out.println();
            System.out.println("=========== [메뉴별 판매현황] ==========");
            System.out.println("    ☆메뉴 이름☆        ☆판매 수량☆");
            while (rs.next()) {
                System.out.printf("%10s",rs.getString("메뉴이름") );
                System.out.printf("%15s ", rs.getInt("판매수량"));
                System.out.println();
                System.out.println("=====================================================");
                System.out.println();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pStmt);
        Common.close(conn);
    }

    public void vip() {
        String sql = "SELECT 고객이름, COUNT(고객이름) AS 방문횟수\n" +
                "    FROM CUSTOMER C JOIN ORDER_LIST O\n" +
                "        ON C.회원번호 = O.회원번호\n" +
                "        GROUP BY 고객이름\n" +
                "            HAVING COUNT(고객이름) IN  (SELECT MAX(COUNT(고객이름))\n" +
                "                                    FROM CUSTOMER C JOIN ORDER_LIST O\n" +
                "                                    ON C.회원번호 = O.회원번호\n" +
                "                                    GROUP BY 고객이름) " +
                "ORDER BY COUNT(고객이름) DESC ";

        try {
            conn = Common.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.executeUpdate();
            rs = pStmt.getResultSet();
            int num = 1;
            System.out.println();
            System.out.println("=========== [ohoh 우리의 ☆VIP★ ohoh] ==========");
            System.out.println("    ☆찬란한 우리의 고객님☆     ☆방문횟수☆");
            while (rs.next()) {

                System.out.printf("%-10s" , num + "등");
                System.out.printf("%-3s", rs.getString("고객이름") );
                System.out.printf("%15s ", rs.getInt("방문횟수")+"회");
                System.out.println();
                System.out.println("=====================================================");
                System.out.println();
                num++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(pStmt);
        Common.close(conn);
    }


    public void checkList() { // 월별 재료 소진 확인
        int month;
        String sql;
        System.out.println();
        System.out.print("※ 조회할 달을 입력하세요 \n ☞ ");
        month = sc.nextInt();
        System.out.println();
        while (true) {
            System.out.println("※ 조회할 재료명 번호를 입력하세요");
            System.out.print("[1] 빵  [2] 치즈  [3] 야채  [4] 소스  [5] 종료\n ☞ ");
            int selNum = sc.nextInt();
            switch (selNum) {

                case 1:

                sql = "SELECT 빵, COUNT(빵)\n" +
                        "    FROM ORDER_LIST\n" +
                        "    WHERE EXTRACT(MONTH FROM 주문날짜) = ?\n" +
                        "    GROUP BY 빵\n" +
                        "    ORDER BY 2 DESC";
                try {
                    conn = Common.getConnection();
                    pStmt = conn.prepareStatement(sql);
                    pStmt.setInt(1, month);
                    pStmt.executeUpdate();
                    rs = pStmt.getResultSet();
                    System.out.println();
                    System.out.println("======= [" + month + "월 빵 소진 내역] =======");
                    System.out.println("  빵      수량");
                    System.out.println("----------------");
                    while (rs.next()) {
                        System.out.printf("%-10s", rs.getString("빵"));
                        System.out.print(rs.getInt("COUNT(빵)"));
                        System.out.println();
                    }
                    System.out.println("----------------");
                    System.out.println();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Common.close(rs);
                Common.close(pStmt);
                Common.close(conn);
                break;

                case 2:
                   // System.out.print("조회할 달을 입력하세요 \n ☞ ");
                   // month = sc.nextInt();
                    sql = "SELECT 치즈, COUNT(치즈)\n" +
                            "    FROM ORDER_LIST\n" +
                            "    WHERE EXTRACT(MONTH FROM 주문날짜) = ?\n" +
                            "    GROUP BY 치즈\n" +
                            "    ORDER BY 2 DESC";
                    try {
                        conn = Common.getConnection();
                        pStmt = conn.prepareStatement(sql);
                        pStmt.setInt(1, month);
                        pStmt.executeUpdate();
                        rs = pStmt.getResultSet();
                        System.out.println();
                        System.out.println("======= [" + month + "월 치즈 소진 내역] =======");
                        System.out.println("  치즈      수량");
                        System.out.println("----------------");
                        while (rs.next()) {
                            System.out.printf("%-10s", rs.getString("치즈"));
                            System.out.print(rs.getInt("COUNT(치즈)"));
                            System.out.println();
                        }
                        System.out.println("----------------");
                        System.out.println();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Common.close(rs);
                    Common.close(pStmt);
                    Common.close(conn);
                    break;

                case 3 :
                  //  System.out.print("조회할 달을 입력하세요 \n ☞ ");
                  //  month = sc.nextInt();
                    sql = "SELECT 야채, COUNT(야채)\n" +
                            "    FROM ORDER_LIST\n" +
                            "    WHERE EXTRACT(MONTH FROM 주문날짜) = ?\n" +
                            "    GROUP BY 야채\n" +
                            "    ORDER BY 2 DESC";
                    try {
                        conn = Common.getConnection();
                        pStmt = conn.prepareStatement(sql);
                        pStmt.setInt(1, month);
                        pStmt.executeUpdate();
                        rs = pStmt.getResultSet();
                        System.out.println();
                        System.out.println("======= [" + month + "월 야채 소진 내역] =======");
                        System.out.println("  야채      수량");
                        System.out.println("----------------");
                        while (rs.next()) {
                            System.out.printf("%-10s", rs.getString("야채"));
                            System.out.print(rs.getInt("COUNT(야채)"));
                            System.out.println();
                        }
                        System.out.println("----------------");
                        System.out.println();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Common.close(rs);
                    Common.close(pStmt);
                    Common.close(conn);
                    break;
                case 4 :
                  //  System.out.print("조회할 달을 입력하세요 \n ☞ ");
                  //  month = sc.nextInt();
                    sql = "SELECT 소스, COUNT(소스)\n" +
                            "    FROM ORDER_LIST\n" +
                            "    WHERE EXTRACT(MONTH FROM 주문날짜) = ?\n" +
                            "    GROUP BY 소스\n" +
                            "    ORDER BY 2 DESC";
                    try {
                        conn = Common.getConnection();
                        pStmt = conn.prepareStatement(sql);
                        pStmt.setInt(1, month);
                        pStmt.executeUpdate();
                        rs = pStmt.getResultSet();
                        System.out.println();
                        System.out.println("======= [" + month + "월 소스 소진 내역] =======");
                        System.out.println("  소스      수량");
                        System.out.println("----------------");
                        while (rs.next()) {
                            System.out.printf("%-10s", rs.getString("소스"));
                            System.out.print(rs.getInt("COUNT(소스)"));
                            System.out.println();
                        }
                        System.out.println("----------------");
                        System.out.println();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Common.close(rs);
                    Common.close(pStmt);
                    Common.close(conn);
                    break;

                case 5 :
                    System.out.println("재료소진 확인을 종료합니다");
                    System.out.println();
                    return;

                default:
                    System.out.println("선택하신 번호를 다시 확인하세요.");
                    System.out.println();
                    selNum = 0;
                    continue;

            }
        }
    }
}