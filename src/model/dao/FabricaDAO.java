package model.dao;

import db.DB;
import model.dao.impl.VendedorDAOJDBC;
import model.dao.impl.DepartamentoDAOJDBC;

public class FabricaDAO {

	public static VendedorDAO criarVendedorDAO() {
		return new VendedorDAOJDBC(DB.getConnection());
	}
	
	public static DepartamentoDAO criarDepartamentoDAO() {
		return new DepartamentoDAOJDBC(DB.getConnection());
	}
	
}
