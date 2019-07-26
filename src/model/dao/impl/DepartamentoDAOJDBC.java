package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDAO;
import model.entities.Departamento;

public class DepartamentoDAOJDBC implements DepartamentoDAO{

	private Connection conn;	
		
	public DepartamentoDAOJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Departamento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO departamento (nome) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro inesperado! Nenhuma linha afetada!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void update(Departamento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE departamento " +
							"SET nome = ? WHERE id_departamento = ?");
			
			st.setString(1, obj.getNome());			
			st.setInt(2, obj.getId());
			
			st.executeUpdate();		
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM departamento WHERE id_departamento = ?");
			
			st.setInt(1, id);
			
			int linhas = st.executeUpdate();
			
			if(linhas == 0) {
				throw new DbException("ID não existe!");
			}
			
		} catch (SQLException e){
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public Departamento findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM departamento WHERE id_departamento = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento dep = new Departamento();
				dep.setId(rs.getInt("id_departamento"));
				dep.setNome(rs.getString("nome"));
				return dep;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	

	@Override
	public List<Departamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM departamento ORDER BY nome");
			rs = st.executeQuery();
			List<Departamento> listaDep = new ArrayList<>();
			while (rs.next()) {				
				Departamento dep = new Departamento();
				dep.setId(rs.getInt("id_departamento"));
				dep.setNome(rs.getString("nome"));				
				listaDep.add(dep);
			}
			return listaDep;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
