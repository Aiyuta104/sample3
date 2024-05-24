package la.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import la.bean.CategoryBean;
import la.bean.ItemBean;

public class ItemDAO {
    // URL、ユーザ名、パスワードの準備
    private String url = "jdbc:postgresql:sample";
    private String user = "student";
    private String pass = "himitu";

    public ItemDAO() throws DAOException {
        try {
			// JDBCドライバの登録
			Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new DAOException("JDBCドライバの登録に失敗しました。");
        }
    }

    public List<CategoryBean> findAllCategory() throws DAOException {
        // SQL文の作成
        String sql = "SELECT * FROM category ORDER BY code";
		
        try (// データベースへの接続
             Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);
			 // SQLの実行
			 ResultSet rs = st.executeQuery();) {
            // 結果の取得および表示
			List<CategoryBean> list = new ArrayList<CategoryBean>();
			while (rs.next()) {
			    int code = rs.getInt("code");
			    String name = rs.getString("name");
			    CategoryBean bean = new CategoryBean(code, name);
			    list.add(bean);
			}
			// カテゴリ一覧をListとして返す
			return list;
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの取得に失敗しました。");
        } 
    }

    public List<ItemBean> findByCategory(int categoryCode)
                                                throws DAOException {
        // SQL文の作成
        String sql =
            "SELECT * FROM item WHERE category_code = ? ORDER BY code";
		
        try (// データベースへの接続
             Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);) {
			// カテゴリの設定
			st.setInt(1, categoryCode);
			
			try (// SQLの実行
			     ResultSet rs = st.executeQuery();) {
			    // 結果の取得および表示
			    List<ItemBean> list = new ArrayList<ItemBean>();
			    while (rs.next()) {
			        int code = rs.getInt("code");
			        String name = rs.getString("name");
			        int price = rs.getInt("price");
			        ItemBean bean = new ItemBean(code, name, price);
			        list.add(bean);
                }
                // 商品一覧をListとして返す
                return list;
			} catch (SQLException e) {
			    e.printStackTrace();
			    throw new DAOException("レコードの取得に失敗しました。");
            }
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの取得に失敗しました。");
        } 
    }

    public ItemBean findByPrimaryKey(int key) throws DAOException {
        // SQL文の作成
        String sql = "SELECT * FROM item WHERE code = ?";
		
        try (// データベースへの接続
             Connection con = DriverManager.getConnection(url, user, pass);
			 // PreparedStatementオブジェクトの取得
			 PreparedStatement st = con.prepareStatement(sql);) {
			// カテゴリの設定
			st.setInt(1, key);
			
			try (// SQLの実行
			     ResultSet rs = st.executeQuery();) {
			    // 結果の取得および表示
			    if (rs.next()) {
			        int code = rs.getInt("code");
			        String name = rs.getString("name");
			        int price = rs.getInt("price");
			        ItemBean bean = new ItemBean(code, name, price);
			        return bean; // 主キーに該当するレコードを返す
                } else {
			        return null; // 主キーに該当するレコードなし
                }
            } catch (SQLException e) {
			    e.printStackTrace();
			    throw new DAOException("レコードの取得に失敗しました。");
			}
        } catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("レコードの取得に失敗しました。");
        }
    }
}