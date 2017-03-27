package com.icbc.nt.dom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;







import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.MenuBean;

@Repository
public class MenuDao extends DaoParent{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*public int getMatchCount(String userName,String password){
		String sqlStr = "select count(*) from t_user t where t.user_name=? and t.password=?";
		return jdbcTemplate.queryForInt(sqlStr, new Object[]{userName,password});
	}*/
	
	/**
	 * 查询所有menu
	 * @param ja
	 * @param checkVar
	 */
	public void getMenuItemAll(final JSONArray ja){
		final List list = new ArrayList();
//		String sqlStr = "select * from t_ntmisc_menu  t where t.parent_id=?";
		String sqlStr = "select * from t_ntmisc_menu where menu_id !='0'";
		final MenuBean menuBean = new MenuBean();
		jdbcTemplate.query(sqlStr, new Object[] {},
				new RowCallbackHandler() {
//					@Override
					public void processRow(ResultSet rs) throws SQLException {
						JSONObject json = new JSONObject();
						System.out.println(rs.getString("menu_id"));
						json.put("id", rs.getString("menu_id"));
						json.put("parent_id", rs.getString("parent_id"));
						json.put("menu_text", rs.getString("menu_text"));
						json.put("node_type", "0");
						json.put("leaf", rs.getString("node_type").equals("1")?"true":"false");
						json.put("expanded", "true");
						json.put("children",new JSONArray());
//						json.put(key, value);
						ja.add(json);
					}
				});
		return;
	}
	
	
	/**
	 * 查询下级menu
	 * @param ja
	 * @param parentId
	 */
	public void getMenuItem(final JSONArray ja,String parentId,String userId,String strIn){
		final List list = new ArrayList();
		String sqlStr = "select * from t_ntmisc_menu  t where t.parent_id=? and t.menu_id in "
				+ strIn
				+" order by menu_id";
//		String sqlStr = "select * from t_ntmisc_menu  t where t.parent_id=?";
		final MenuBean menuBean = new MenuBean();
		jdbcTemplate.query(sqlStr, new Object[] {parentId},
				new RowCallbackHandler() {
//					@Override
					public void processRow(ResultSet rs) throws SQLException {
						menuBean.setMenuId(rs.getString("menu_id"));
						menuBean.setMenuText(rs.getString("menu_text"));
						menuBean.setNodeType(rs.getString("node_type"));
						menuBean.setParentId(rs.getString("parent_id"));
						menuBean.setUrlText(rs.getString("url_text"));
						if(rs.getString("node_type").equals("0")){
							menuBean.setLeaf(false);
						}else{
							menuBean.setLeaf(true);
						}
						JSONObject json = (JSONObject) JSONObject.toJSON(menuBean);
						ja.add(json);
					}
				});
		return;
	}

	@Override
	public int countQuery(String sqlStr, Object[] obj) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*public void updateLoginInfo(User user){
		String sqlStr = "update t_user set last_visit=?,last_ip=?,credits=?  where user_id=?";
		jdbcTemplate.update(sqlStr, new Object[]{user.getLastVisit(),
				user.getLastIp(),user.getCredits(),user.getUserId()});
	}	*/
}
