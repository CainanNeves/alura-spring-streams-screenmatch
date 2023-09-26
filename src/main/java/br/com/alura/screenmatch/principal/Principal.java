package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner input = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=%s&apikey=6585022c";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para ");
        var nomeSerie = input.nextLine();
        String address = String.format(ENDERECO, nomeSerie.replace(" ", "+"));
        String json = consumoAPI.obterDados(address);
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalSeasons(); i++) {
            address = String.format(ENDERECO, nomeSerie.replace(" ", "+") + "&season=" + i);
            json = consumoAPI.obterDados(address);
            DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);

//        for (Temporada temporada : temporadas) {
//                List<Episodio> episodiosTemporada = temporada.episodios();
//            for (Episodio episodio : episodiosTemporada) {
//                System.out.println(episodio.title());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.title())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                //.toList() retorna uma coleção imutável
                .collect(Collectors.toList());

        System.out.println("\nTop 10 episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::rating).reversed())
                .limit(10)
                .forEach(System.out::println);

        System.out.println("\nUsando temporada para criar episodios");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.seasonNumber(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("A partir de que ano?");
        var ano = input.nextInt();
        input.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getSeason() +
                                " Episodio: " + e.getTitle() +
                                " Data lancamento: " + e.getReleaseDate().format(formatador)
                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getSeason,
                        Collectors.averagingDouble(Episodio::getRating)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getRating));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
