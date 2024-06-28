
import controller.Aluno;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Caminho relativo para o arquivo alunos.csv
        String caminhoAlunos = "src/data/alunos.csv";

        List<Aluno> alunos = lerAlunosDoArquivo(caminhoAlunos);

        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno encontrado no arquivo.");
            return;
        }

        int totalAlunos = alunos.size();
        int aprovados = 0;
        int reprovados = 0;
        double menorNota = Double.MAX_VALUE;
        double maiorNota = Double.MIN_VALUE;
        double somaNotas = 0.0;

        Aluno melhorAluno = null;
        Aluno piorAluno = null;

        for (Aluno aluno : alunos) {
            if (aluno.isAprovado()) {
                aprovados++;
            } else {
                reprovados++;
                if (piorAluno == null || aluno.getNota() < piorAluno.getNota()) {
                    piorAluno = aluno; // Atualiza o pior aluno (reprovado com menor nota)
                }
            }

            // Verifica menor e maior nota
            if (aluno.getNota() < menorNota) {
                menorNota = aluno.getNota();
            }
            if (aluno.getNota() > maiorNota) {
                maiorNota = aluno.getNota();
                melhorAluno = aluno; // Atualiza o melhor aluno (maior nota)
            }

            // Soma das notas para calcular a média
            somaNotas += aluno.getNota();
        }

        // Calcular média geral
        double mediaGeral = somaNotas / totalAlunos;

        // Exibir informações na tela
        System.out.println("----- Resumo da Turma -----");
        System.out.println("Total de Alunos: " + totalAlunos);
        System.out.println("Aprovados: " + aprovados);
        System.out.println("Reprovados: " + reprovados);

        if (piorAluno != null) {
            System.out.println("Pior aluno (Reprovado com menor nota): " + piorAluno.getNome() + " - Nota: " + piorAluno.getNota());
        }

        if (melhorAluno != null) {
            System.out.println("Melhor aluno (Maior nota): " + melhorAluno.getNome() + " - Nota: " + melhorAluno.getNota());
        }

        System.out.println("Média Geral da Turma: " + mediaGeral);

        // Caminho absoluto para o arquivo resumo.csv
        String caminhoResumo = "src/data/resumo.csv";

        // Escrever resumo no arquivo resumo.csv
        escreverResumo(caminhoResumo, totalAlunos, aprovados, reprovados, menorNota, maiorNota, mediaGeral);

        System.out.println("Resumo gravado com sucesso no arquivo resumo.csv");
    }

    public static List<Aluno> lerAlunosDoArquivo(String nomeArquivo) {
        List<Aluno> alunos = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(nomeArquivo))) {
            // Ignorar a primeira linha (cabeçalho)
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                String[] campos = linha.split(";");

                // Verificar se a linha possui todos os campos esperados
                if (campos.length == 3) {
                    int matricula = Integer.parseInt(campos[0].trim());
                    String nome = campos[1].trim();

                    // Substituir vírgula por ponto na nota
                    String notaStr = campos[2].trim().replace(',', '.');
                    double nota = Double.parseDouble(notaStr);

                    Aluno aluno = new Aluno(matricula, nome, nota);
                    alunos.add(aluno);
                } else {
                    System.out.println("Formato inválido da linha: " + linha);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + nomeArquivo);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter número. Verifique o formato da nota.");
            e.printStackTrace();
        }

        return alunos;
    }

    public static void escreverResumo(String nomeArquivo, int totalAlunos, int aprovados, int reprovados,
                                      double menorNota, double maiorNota, double mediaGeral) {
        // Usando um caminho absoluto completo para garantir que o arquivo seja salvo corretamente
        File arquivoResumo = new File(nomeArquivo);

        try (FileWriter writer = new FileWriter(arquivoResumo)) {
            writer.write("Quantidade de Alunos;Aprovados;Reprovados;Menor Nota;Maior Nota;Média Geral\n");
            writer.write(totalAlunos + ";" + aprovados + ";" + reprovados + ";" +
                    menorNota + ";" + maiorNota + ";" + mediaGeral + "\n");
        } catch (IOException e) {
            System.out.println("Erro ao escrever no arquivo: " + arquivoResumo);
            e.printStackTrace();
        }
    }
}
