package edu.upc.eetac.dsa.griverola.beeter.api;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import edu.upc.eetac.dsa.griverola.beeter.api.model.Sting;
import edu.upc.eetac.dsa.griverola.beeter.api.model.StingCollection;
 
@Path("/stings")
public class StingResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	public DataSource getDs (){
    	return ds;
    }
	
    public void setDs (DataSource ds){
    	this.ds = ds;
    }



private String GET_STINGS_QUERY = "select s.*, u.name from stings s, users u where u.username=s.username order by creation_timestamp desc";

@GET
@Produces(MediaType.BEETER_API_STING_COLLECTION)
public StingCollection getStings() {
	StingCollection stings = new StingCollection();
 
	Connection conn = null;
	try {
		conn = ds.getConnection();
	} catch (SQLException e) {
		e.printStackTrace();
	}
 
	PreparedStatement stmt = null;
	try {
		stmt = conn.prepareStatement(GET_STINGS_QUERY);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			Sting sting = new Sting();
			sting.setStingid(rs.getInt("stingid"));
			sting.setUsername(rs.getString("username"));
			sting.setAuthor(rs.getString("name"));
			sting.setSubject(rs.getString("subject"));
			sting.setLastModified(rs.getTimestamp("last_modified")
					.getTime());
			sting.setCreationTimestamp(rs
					.getTimestamp("creation_timestamp").getTime());
			stings.addSting(sting);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			conn.close();
		} catch (SQLException e) {
		}
	}
 
	return stings;
}
private String INSERT_STING_QUERY = "insert into stings (username, subject, content) values (?, ?, ?)";

@POST
@Consumes(MediaType.BEETER_API_STING)
@Produces(MediaType.BEETER_API_STING)
public Sting createSting(Sting sting) {
	Connection conn = null;
	try {
		conn = ds.getConnection();
	} catch (SQLException e) {
		e.printStackTrace();
	}
 
	PreparedStatement stmt = null;
	try {
		stmt = conn.prepareStatement(INSERT_STING_QUERY,
				Statement.RETURN_GENERATED_KEYS);
 
		stmt.setString(1, sting.getUsername());
		stmt.setString(2, sting.getSubject());
		stmt.setString(3, sting.getContent());
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		if (rs.next()) {
			int stingid = rs.getInt(1);
 
			sting = getSting(Integer.toString(stingid));
		} else {
			// Something has failed...
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			conn.close();
		} catch (SQLException e) {
		}
	}
 
	return sting;
}
private String GET_STING_BY_ID_QUERY = "select s.*, u.name from stings s, users u where u.username=s.username and s.stingid=?";

@GET
@Path("/{stingid}")
@Produces(MediaType.BEETER_API_STING)
public Sting getSting(@PathParam("stingid") String stingid) {
	Sting sting = new Sting();
 
	Connection conn = null;
	try {
		conn = ds.getConnection();
	} catch (SQLException e) {
		e.printStackTrace();
	}
 
	PreparedStatement stmt = null;
	try {
		stmt = conn.prepareStatement(GET_STING_BY_ID_QUERY);
		stmt.setInt(1, Integer.valueOf(stingid));
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			sting.setStingid(rs.getInt("stingid"));
			sting.setUsername(rs.getString("username"));
			sting.setAuthor(rs.getString("name"));
			sting.setSubject(rs.getString("subject"));
			sting.setContent(rs.getString("content"));
			sting.setLastModified(rs.getTimestamp("last_modified")
					.getTime());
			sting.setCreationTimestamp(rs
					.getTimestamp("creation_timestamp").getTime());
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			conn.close();
		} catch (SQLException e) {
		}
	}
 
	return sting;
}

}

