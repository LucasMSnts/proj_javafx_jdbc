package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.VendedorDAO;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDAOJDBC implements VendedorDAO{

	private Connection conn;
	
	public VendedorDAOJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Vendedor obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO vendedor " + 
					"(nome, email, dataNasc, basesalario, DepartamentoId) " + 
					"VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataNasc().getTime()));
			st.setDouble(4, obj.getBaseSalario());
			st.setInt(5, obj.getDepartamento().getId());
			
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
	public void update(Vendedor obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE vendedor " +
							"SET nome = ?, email = ?, dataNasc = ?, basesalario = ?, DepartamentoId = ? " + 
							"WHERE id_vendendor = ?");
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataNasc().getTime()));
			st.setDouble(4, obj.getBaseSalario());
			st.setInt(5, obj.getDepartamento().getId());
			st.setInt(6, obj.getId());
			
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
			st = conn.prepareStatement("DELETE FROM vendedor WHERE id_vendendor = ?");
			
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
	public Vendedor findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT vendedor.*, departamento.nome as DepNome " + 
					"FROM vendedor INNER JOIN departamento " + 
					"ON vendedor.DepartamentoId = departamento.id_departamento " + 
					"WHERE vendedor.id_vendendor = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento dep = instanciaDepartamento(rs);				
				Vendedor ven = instanciaVendedor(rs, dep);
				return ven;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}

	private Vendedor instanciaVendedor(ResultSet rs, Departamento dep) throws SQLException {
		Vendedor ven = new Vendedor();
		ven.setId(rs.getInt("id_vendendor"));
		ven.setNome(rs.getString("nome"));
		ven.setEmail(rs.getString("email"));
		ven.setDataNasc(rs.getDate("dataNasc"));
		ven.setBaseSalario(rs.getDouble("basesalario"));
		ven.setDepartamento(dep);
		return ven;
	}

	private Departamento instanciaDepartamento(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("DepartamentoId"));
		dep.setNome(rs.getString("DepNome"));
		return dep;
	}

	@Override
	public List<Vendedor> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT vendedor.*,departamento.nome as DepNome " + 
					"FROM vendedor INNER JOIN departamento " + 
					"ON vendedor.DepartamentoId = departamento.id_departamento " + 
					"ORDER BY nome");
			rs = st.executeQuery();
			List<Vendedor> listaVen = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			while (rs.next()) {
				
				Departamento dep = map.get(rs.getInt("DepartamentoId"));
				
				if (dep == null) {
					dep = instanciaDepartamento(rs);
					map.put(rs.getInt("DepartamentoID"), dep);
				}				
								
				Vendedor ven = instanciaVendedor(rs, dep);
				listaVen.add(ven);
			}
			return listaVen;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Vendedor> findByDepartamento(Departamento departamento) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT vendedor.*,departamento.nome as DepNome " + 
					"FROM vendedor INNER JOIN departamento " + 
					"ON vendedor.DepartamentoId = departamento.id_departamento " + 
					"WHERE DepartamentoId = ? " + 
					"ORDER BY nome");
			st.setInt(1, departamento.getId());
			rs = st.executeQuery();
			List<Vendedor> listaVen = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			while (rs.next()) {
				
				Departamento dep = map.get(rs.getInt("DepartamentoId"));
				
				if (dep == null) {
					dep = instanciaDepartamento(rs);
					map.put(rs.getInt("DepartamentoID"), dep);
				}				
								
				Vendedor ven = instanciaVendedor(rs, dep);
				listaVen.add(ven);
			}
			return listaVen;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}	
	}

}
