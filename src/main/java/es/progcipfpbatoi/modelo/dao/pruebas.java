package es.progcipfpbatoi.modelo.dao;

import es.progcipfpbatoi.exceptions.DatabaseErrorException;


public class pruebas {
    public static void main(String[] args) throws DatabaseErrorException {
        FileTareaDAO fileTareaDAO = new FileTareaDAO();

        fileTareaDAO.findAll();
        fileTareaDAO.findAll("sacar al p");
    }
}
