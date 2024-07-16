package com.aluracursos.literalura.model;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")

public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Column(unique = true)
    private String titulo;
    private String idioma;
    private Double numeroDescargas;
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Libro() {}

    public Libro(DatosLibro datosLibros) {
        this.titulo = datosLibros.titulo();
        this.idioma = datosLibros.idioma().get(0);
        this.numeroDescargas = datosLibros.numeroDescargas();
    }

    @Override
    public String toString() {
        return  "\n-------DATOS LIBRO-------" +
                "\nTitulo: " + titulo +
                "\nAutor: " + autor.getNombre() +
                "\nIdioma: " + idioma +
                "\nNumero de descargas: " + numeroDescargas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
}
