package es.progcipfpbatoi.modelo.dao;

import es.progcipfpbatoi.exceptions.DatabaseErrorException;
import es.progcipfpbatoi.exceptions.NotFoundException;
import es.progcipfpbatoi.modelo.dto.Categoria;
import es.progcipfpbatoi.modelo.dto.Tarea;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileTareaDAO implements TareaDAO{


    private static final String DATABASE_FILE = "resources/database/tareas.txt";
    private static final int ID = 0;
    private static final int DESCRIPCION = 1;
    private static final int FECHA = 2;
    private static final int FINALIZADO = 3;
    private static final int CATEGORIA = 4;

    private File file;

    public FileTareaDAO() {
        this.file = new File(DATABASE_FILE);
    }

    @Override
    public ArrayList<Tarea> findAll() {
        ArrayList<Tarea> arrayListTareas= new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DATABASE_FILE));


            do{
                String linea = bufferedReader.readLine();
                if (linea == null) {
                    return arrayListTareas;
                }
                String[] fields = linea.split(";");
                int codigo = Integer.parseInt(fields[ID]);
                String descripcion = fields[DESCRIPCION];
                LocalDateTime fecha = LocalDateTime.parse(fields[FECHA], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                boolean finalizado = Boolean.parseBoolean(fields[FINALIZADO]);
                Categoria categoria = Categoria.parse(fields[CATEGORIA]);
                Tarea tarea = new Tarea(codigo, descripcion, fecha, finalizado, categoria);
                arrayListTareas.add(tarea);
                System.out.println(linea);

            }while (true);
        } catch (IOException e) {
            System.out.println("No se encuentra el archivo");
        }
        return arrayListTareas;
    }


    @Override
    public ArrayList<Tarea> findAll(String text) throws DatabaseErrorException{
        ArrayList<Tarea> arrayListTareas= new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(DATABASE_FILE));


            do{
                String linea = bufferedReader.readLine();
                if (linea == null) {
                    return arrayListTareas;
                }

                String[] fields = linea.split(";");
                int codigo = Integer.parseInt(fields[ID]);
                String descripcion = fields[DESCRIPCION];
                LocalDateTime fecha = LocalDateTime.parse(fields[FECHA], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                boolean finalizado = Boolean.parseBoolean(fields[FINALIZADO]);
                Categoria categoria = Categoria.parse(fields[CATEGORIA]);
                Tarea tarea = new Tarea(codigo, descripcion, fecha, finalizado, categoria);

                if (descripcion.startsWith(text)) {
                    arrayListTareas.add(tarea);
                    System.out.println(linea);
                }
            }while (true);
        } catch (IOException e) {
            throw new DatabaseErrorException("No se encuentra el archivo");
        }
    }

    @Override
    public Tarea getById(int id) throws NotFoundException, DatabaseErrorException {
        try (FileReader fileReader = new FileReader(this.file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            do {
                String register = bufferedReader.readLine();
                if (register == null) {
                    throw new NotFoundException("Tarea no encontrada");
                } else if (!register.isBlank()) {
                    String[] fields = register.split(";");
                    int codigo = Integer.parseInt(fields[ID]);
                    String descripcion = fields[DESCRIPCION];
                    LocalDateTime fecha = LocalDateTime.parse(fields[FECHA], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    boolean finalizado = Boolean.parseBoolean(fields[FINALIZADO]);
                    Categoria categoria = Categoria.parse(fields[CATEGORIA]);
                    Tarea tarea = new Tarea(codigo, descripcion, fecha, finalizado, categoria);
                    if (tarea.getId() == id) {
                        return tarea;
                    }
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DatabaseErrorException("Ocurrió un error en el acceso a la base de datos");
        }
    }

    @Override
    public boolean save(Tarea tarea) {
        ArrayList<Tarea> tareas = findAll();
        try {
            File file = new File(DATABASE_FILE);
            boolean exists = file.exists();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, exists));
            boolean tareaActualizada = false;
            for (Tarea tareaItem: tareas) {
                if (tareaItem.getDescripcion().equals(tarea.getDescripcion())) {
                    String lineaTarea = convertirAString(tarea);
                    bufferedWriter.newLine();
                    bufferedWriter.write(lineaTarea);
                    tareaActualizada = true;
                    System.out.println("Tarea actualizada");
                } else {
                    String lineaTareaItem = convertirAString(tareaItem);
                    bufferedWriter.newLine();
                    bufferedWriter.write(lineaTareaItem);
                }
            }
            if (!tareaActualizada) {
                String lineaTarea = convertirAString(tarea);
                if (exists) {
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(lineaTarea);
                System.out.println("Tarea añadida");
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Fichero no encontrado");
        }
        return true;
    }

    @Override
    public boolean remove(int id) throws DatabaseErrorException, NotFoundException {
        ArrayList<Tarea> tareas = findAll();
        boolean tareaEliminada = false;
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Tarea tareaItem : tareas) {
                if (tareaItem.getId() == id) {
                    tareaEliminada = true;
                } else {
                    String lineaTareaItem = convertirAString(tareaItem);
                    bufferedWriter.write(lineaTareaItem);
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
            if (!tareaEliminada) {
                throw new NotFoundException("La tarea no se encuentra en la base de datos.");
            }
        } catch (IOException e) {
            throw new DatabaseErrorException("Error al acceder al archivo de la base de datos de tareas: " + e.getMessage());
        }
        return true;
    }

    private static String convertirAString(Tarea tarea){
        int codigo= tarea.getId();
        String codigoString= String.valueOf(codigo);
        String descripcion = tarea.getDescripcion();
        LocalDateTime fecha = tarea.getFechaAlta();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaAString = fecha.format(formatter);
        boolean finalizado = tarea.isFinalizada();
        String finalizadoString= String.valueOf(finalizado);
        Categoria categoria = tarea.getCategoria();
        String categoriaString = categoria.name();
        return codigoString+";"+descripcion+";"+fechaAString+";"+finalizadoString+";"+categoriaString;

    }
}
