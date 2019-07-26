package model.services;

import java.util.List;

import model.dao.DepartamentoDAO;
import model.dao.FabricaDAO;
import model.entities.Departamento;

public class DepartamentoService {
	
	private DepartamentoDAO dao = FabricaDAO.criarDepartamentoDAO();
	
	public List<Departamento> findAll(){
		return dao.findAll();
	}
}
