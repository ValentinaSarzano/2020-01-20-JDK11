package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllRoles(){

		String sql = "SELECT DISTINCT role "
				+ "FROM authorship "
				+ "ORDER BY role ASC";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String role = res.getString("role");
				
				result.add(role);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertici(String role, Map<Integer, Artist> idMap) {
		String sql = "SELECT DISTINCT a.artist_id, a.name "
				+ "FROM artists a, authorship au "
				+ "WHERE a.artist_id = au.artist_id "
				+ "AND au.role = ? "
				+ "ORDER BY a.artist_id";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("a.artist_id"))) {
					Artist a = new Artist(res.getInt("a.artist_id"), res.getString("a.name"));
				    idMap.put(res.getInt("a.artist_id"), a);
				}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String role, Map<Integer, Artist> idMap){
		String sql = "SELECT DISTINCT a1.artist_id AS id1, a2.artist_id AS id2, COUNT(e1.exhibition_id) AS peso "
				+ "FROM artists a1, artists a2, authorship au1, authorship au2, exhibition_objects e1, exhibition_objects e2 "
				+ "WHERE a1.artist_id < a2.artist_id "
				+ "AND au1.artist_id = a1.artist_id AND au2.artist_id = a2.artist_id "
				+ "AND au1.role = au2.role AND au1.role = ? "
				+ "AND e1.object_id = au1.object_id AND e2.object_id = au2.object_id "
				+ "AND e1.exhibition_id = e2.exhibition_id "
				+ "GROUP BY id1, id2";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getInt("id1")) && idMap.containsKey(res.getInt("id2"))){
					Adiacenza a = new Adiacenza(idMap.get(res.getInt("id1")), idMap.get(res.getInt("id2")), res.getInt("peso"));
				    result.add(a);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
