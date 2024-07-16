package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.IAutorRepository;
import com.aluracursos.literalura.repository.ILibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private ILibroRepository libroRepository;
    private IAutorRepository autorRepository;
    private List<Libro> libros;
    private List<Autor> autores;
    private List<String> idiomas;

    public Principal(ILibroRepository libroRepository, IAutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        int opcion = 1;
        while (opcion != 0) {
            var menu = """
                    --------- MENU ---------
                    1. Busca un libro por titulo
                    2. Lista los libros registrados
                    3. Lista a los autores registrados
                    4. Lista a los autores según año especifico
                    5. Lista libros por idioma específico
                    6. Estadistica de libros que fueron agregados
                    7. Top 10 libros mas descargados
                    8. Buscar un autor registrado
                    0- Salir
                    ---------------------------------------------
                    Selecciona una opcion
                    """;
            System.out.println(menu);
            if (teclado.hasNextInt()) {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        listaLibrosRegistrados();
                        break;
                    case 3:
                        listaAutoresRegistrados();
                        break;
                    case 4:
                        listaAutoresVivos();
                        break;
                    case 5:
                        listaLibrosPorIdioma();
                        break;
                    case 6:
                        estadisticaLibrosAgregados();
                        break;
                    case 7:
                        top10LibrosDescargados();
                        break;
                    case 8:
                        buscarAutorAgregado();
                        break;
                    case 0:
                        System.out.println("Gracias por usar el programa!");
                        break;
                    default:
                        System.out.println("Opcion no valida, revisa las opciones disponibles");
                }
            } else {
                System.out.println("Opción no válida, revisa las opciones disponibles");
                teclado.next();
            }
        }
    }

    private void buscarLibro() {
        System.out.println("Ingresa el nombre del libro: ");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "+"));
        guardarDatos(json);
    }

    private void guardarDatos(String json) {
        try {
            DatosAutor datosAutor = convierteDatos.obtenerDatos(json, DatosAutor.class);
            DatosLibro datosLibro = convierteDatos.obtenerDatos(json, DatosLibro.class);
            //virefica si el autor y libro existe
            Autor autor = autorRepository.findByNombre(datosAutor.nombre())
                    .orElseGet(() -> autorRepository.save(new Autor(datosAutor)));

            if (libroRepository.findByTitulo(datosLibro.titulo()).isEmpty()) {
                Libro libro = new Libro(datosLibro);
                libro.setAutor(autor);
                libroRepository.save(libro);
                System.out.println(libro);
                System.out.println("Libro fue agregado exitosamente");

            } else {
               System.out.println("El libro ya se encuentra registrado");
            }
        } catch (NullPointerException e) {
            System.out.println("El libro no fue encontrado");
        }
    }

    private void listaLibrosRegistrados() {
        libros = libroRepository.findAll();
        libros.stream().forEach(System.out::println);
    }

    private void listaAutoresRegistrados() {
        autores = autorRepository.findAll();
        autores.stream().forEach(System.out::println);
    }

    private void listaAutoresVivos() {
        System.out.println("Ingresa el año en el que deseas buscar: ");
        int fecha = teclado.nextInt();
        autores = autorRepository.autoresPorFechaDeMuerte(fecha);
        if (autores.isEmpty()) {
            System.out.println("Ningun autor estuvo vivo en ese año ");
        } else {
            autores.stream().forEach(System.out::println);
        }
    }

    public void listaLibrosPorIdioma() {
        idiomas = libroRepository.idiomasLibros();
        System.out.printf("----------IDIOMAS----------\n");
        idiomas.stream().forEach(System.out::println);
        System.out.printf("--------------------");
        System.out.println("Ingresa el idioma de los libres que deseas buscar: ");
        var idiomaSeleccionado = teclado.nextLine().toLowerCase();
        libros = libroRepository.librosPoridioma(idiomaSeleccionado);
        if (libros.isEmpty()) {
            System.out.println("Opcion no valida.");
        } else {
            libros.stream().forEach(System.out::println);
        }
    }

    public void estadisticaLibrosAgregados() {
        DoubleSummaryStatistics estadictica = libroRepository.findAll().stream()
                .filter(l -> l.getNumeroDescargas() > 0)
                .collect(Collectors.summarizingDouble(Libro::getNumeroDescargas));
        System.out.println("Cantidad media de descargas: " + estadictica.getAverage());
        System.out.println("Cantidad maxima de descargas: " + estadictica.getMax());
        System.out.println("Cantidad minima de descargas: " + estadictica.getMin());
        System.out.println("Cantidad de registros evaluados para el calculo las estadisticas: " + estadictica.getCount());
    }

    public void top10LibrosDescargados() {
        libroRepository.findTop10ByOrderByNumeroDeDescargasDesc().forEach(System.out::println);
    }

    public void buscarAutorAgregado() {
        System.out.println("Ingresa el nombre del autor que deseas buscar: ");
        var nombreAutor = teclado.nextLine();
        var autor = autorRepository.findByNombre(nombreAutor);
        if (autor.isEmpty()) {
            System.out.println("El autor no se encuentra registrado");
        } else {
            System.out.println(autor);
        }
    }

}
