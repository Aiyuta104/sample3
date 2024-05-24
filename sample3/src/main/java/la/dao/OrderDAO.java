package la.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import la.bean.CartBean;
import la.bean.CustomerBean;
import la.bean.ItemBean;

public class OrderDAO {
    // URL、ユーザ名、パスワードの準備
    private String url = "jdbc:postgresql:sample";
    private String user = "student";
    private String pass = "himitu";

    public OrderDAO() throws DAOException {
        try {
            // JDBCドライバの登録
			Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new DAOException("JDBCドライバの登録の失敗しました。");
        }
    }

    public int saveOrder(CustomerBean customer, CartBean cart)
			                                    throws DAOException {
        // 顧客番号の取得 Serial型の暗黙シーケンスから取得
        int customerNumber = 0;
        String sql = "SELECT nextval('customer_code_seq')";

        try (// データベースへの接続
             Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);
			 // SQLの実行
			 ResultSet rs = st.executeQuery();) {
			if (rs.next()) {
			    customerNumber = rs.getInt(1);
			}
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの操作に失敗しました。");
        }

        // 顧客情報の追加SQL文
        sql = "INSERT INTO customer VALUES(?, ?, ?, ?, ?)";
		
        try (// データベースへの接続
			 Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);) {
			// プレースホルダーの設定
			st.setInt(1, customerNumber);
			st.setString(2, customer.getName());
			st.setString(3, customer.getAddress());
			st.setString(4, customer.getTel());
			st.setString(5, customer.getEmail());
			// SQLの実行
			st.executeUpdate();
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの操作に失敗しました。");
        }

        // 注文番号の取得 Serial型の暗黙シーケンスから取得
        int orderNumber = 0;
        sql = "SELECT nextval('ordered_code_seq')";
		
        try (// データベースへの接続
			 Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);
			 // SQLの実行
			 ResultSet rs = st.executeQuery();) {
			if (rs.next()) {
			    orderNumber = rs.getInt(1);
			}
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの操作に失敗しました。");
        }

        // 注文情報のOrderedテーブルへの追加
        sql = "INSERT INTO ordered VALUES(?, ?, ?, ?)";
		
        try (// データベースへの接続
			 Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);) {
			// プレースホルダーの設定
			st.setInt(1, orderNumber);
			st.setInt(2, customerNumber);
			Date today = new Date(System.currentTimeMillis());
			st.setDate(3, today);
			st.setInt(4, cart.getTotal());
			// SQLの実行
			st.executeUpdate();
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの操作に失敗しました。");
        }
		
        // 注文明細情報のOrderedDetailテーブルへの追加
        // 商品ごとに複数レコード追加
        sql = "INSERT INTO ordered_detail VALUES(?, ?, ?)";
		
        try (// データベースへの接続
			 Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);) {
			List<ItemBean> items = cart.getItems();
			for (ItemBean item : items) {
			    st.setInt(1, orderNumber);
			    st.setInt(2, item.getCode());
			    st.setInt(3, item.getQuantity());
			    st.executeUpdate();
			}
			return orderNumber;
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの操作に失敗しました。");
        } 
    }
}