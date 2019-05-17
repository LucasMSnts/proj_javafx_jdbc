package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartamentoService {
	
	// teste
	public List<Departamento> findAll(){
		List<Departamento> lista = new ArrayList<>();
		lista.add(new Departamento(1, "Computadores"));
		lista.add(new Departamento(2, "Impressoras"));
		lista.add(new Departamento(1, "Eletronicos"));
		return lista;
	}
}
