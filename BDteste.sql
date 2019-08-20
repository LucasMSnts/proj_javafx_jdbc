-- --------------------------------------------------------
-- Servidor:                     127.0.0.1
-- Versão do servidor:           5.5.45 - MySQL Community Server (GPL)
-- OS do Servidor:               Win32
-- HeidiSQL Versão:              9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Copiando estrutura do banco de dados para cursojdbc
CREATE DATABASE IF NOT EXISTS `cursojdbc` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */;
USE `cursojdbc`;

-- Copiando estrutura para tabela cursojdbc.departamento
CREATE TABLE IF NOT EXISTS `departamento` (
  `id_departamento` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id_departamento`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

-- Copiando dados para a tabela cursojdbc.departamento: ~6 rows (aproximadamente)
/*!40000 ALTER TABLE `departamento` DISABLE KEYS */;
INSERT INTO `departamento` (`id_departamento`, `nome`) VALUES
	(1, 'Computadores'),
	(2, 'teste'),
	(3, 'Roupas'),
	(4, 'Livros'),
	(5, 'Comida'),
	(6, 'Musica');
/*!40000 ALTER TABLE `departamento` ENABLE KEYS */;

-- Copiando estrutura para tabela cursojdbc.vendedor
CREATE TABLE IF NOT EXISTS `vendedor` (
  `id_vendendor` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(60) NOT NULL,
  `email` varchar(100) NOT NULL,
  `dataNasc` datetime NOT NULL,
  `basesalario` double NOT NULL,
  `DepartamentoId` int(11) NOT NULL,
  PRIMARY KEY (`id_vendendor`),
  KEY `IX_Seller_DepartmentId` (`DepartamentoId`),
  CONSTRAINT `FKdepartamentoID` FOREIGN KEY (`DepartamentoId`) REFERENCES `departamento` (`id_departamento`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- Copiando dados para a tabela cursojdbc.vendedor: ~7 rows (aproximadamente)
/*!40000 ALTER TABLE `vendedor` DISABLE KEYS */;
INSERT INTO `vendedor` (`id_vendendor`, `nome`, `email`, `dataNasc`, `basesalario`, `DepartamentoId`) VALUES
	(1, 'Isaias Kane', 'bob@gmail.com', '1998-04-21 00:00:00', 2090, 1),
	(2, 'Maria José', 'maria@gmail.com', '1979-12-31 00:00:00', 3890, 2),
	(3, 'Alex Grey', 'alex@gmail.com', '1988-01-15 00:00:00', 2090, 1),
	(4, 'Martha Wayne', 'martha@gmail.com', '1993-11-30 00:00:00', 3000, 4),
	(5, 'Donald Blue', 'donald@gmail.com', '2000-01-09 00:00:00', 4000, 3),
	(6, 'Alexandre Dria', 'bob@gmail.com', '1997-03-04 00:00:00', 3890, 2),
	(7, 'Joao Silva', 'joao@gmail.com', '1988-04-22 00:00:00', 2999, 4);
/*!40000 ALTER TABLE `vendedor` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
