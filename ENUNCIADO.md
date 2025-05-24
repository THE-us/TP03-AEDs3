O terceiro trabalho prático da disciplina também envolverá implementar as buscas por termos das entidades (séries, episódios e atores) usando as listas invertidas.

ÍNDICE INVERTIDO

O índice invertido nos permite fazer buscas por entidades a partir de seus termos (palavras). Para isso, a gente precisa criar uma lista de IDs para cada termo indexado. Por exemplo, suponha que, em um sistema que gerencia livros, os seguintes livros sejam inseridos:
ID 	Título
1 	Programação de Computadores
2 	Introdução aos Computadores
3 	Introdução à Programação
4 	Programação básica: uma introdução à programação

A criação das listas começa com a inserção do primeiro livro. Ao criá-lo, as palavras do título (ou outro atributo) devem ser transformadas em um vetor de palavras. Em seguida, as palavras irrelevantes para as buscas, como os artigos, as preposições e os numerais devem ser descartados desse vetor. Essas palavras irrelevantes são chamadas de stop words (palavras vazias). Vocês podem encontrar listas de stop words em português na Internet. Em seguida, é necessário aplicar uma transformação para que as palavras restantes fiquem em letras minúsculas e sem acentos. Por exemplo, para o primeiro livro, o seguinte vetor será gerado: [ "programacao", "computadores" ].

Empregando a lógica do valor TFxIDF

Links to an external site. (Term Frequency — Inverse Document Frequency), o próximo passo é determinar a frequência do termo no título e qual o inverso da frequência do termo entre os títulos. O valor TF indica a taxa de ocorrências de um determinado termo no título. Por exemplo, para o primeiro livro, o termo "programacao" aparece uma vez de um total de dois termos (50% = 0.5). Já para o livro quatro, esse termo aparece 2 vezes em um total de 4 termos (já descartadas as stop words) e seu valor também é 0.5. No entanto, o termo "basica" aparece 1 vez em quatro palavras e sua frequência é, portanto, 0.25 (25%).

Já o IDF indica o inverso da taxa de ocorrências de um termo entre os títulos (ou entre as entidades). Por exemplo, o termo "programacao" aparece em 3 dos 4 livros. Como buscamos o inverso dessa taxa, o IDF desse termo será 4/3 = 1.333. No entanto, para equilibrarmos os resultados, aplicaremos a função logarítmo a essa valor e somaremos o valor 1 ao resultado, para que todas as respostas sejam acima de 1. Assim, o IDF do termo será efetivamente: log(4/3)+1 = 1.125

Após a inserção de todos os livros, chegaremos às seguintes listas invertidas:

- basica: (4; 0.25)
- computadores: (1; 0.5), (2; 0.5)
- introducao: (2; 0.5), (3; 0.5), (4; 0.25)
- programacao: (1; 0.5), (3; 0.5), (4; 0.5)

Note que as frequências foram registradas como uma taxa das palavras válidas (que não são stop words). Note que nessas listas não aparecem os valores de IDF, mas apenas os valores de TF. Mas sabemos que temos 4 livros (isso deve estar armazenado em algum lugar) e sabemos o tamanho de cada lista. Assim, podemos calcular o IDF a qualquer hora.

Para fazermos a consulta em uma lista assim, precisamos pedir ao usuário que digite os termos de busca. Por exemplo, suponha que ele digita: "Introdução à programação". Quando fizermos o mesmo tratamento que fizemos com os títulos, chegaremos ao vetor [ "introducao", "programacao" ]. Em seguida, recuperamos a lista de cada termo e multiplicamos os valores de TF pelos valores de IDF dos termos. O IDF do termo "introducao" é 1.125, calculado por meio da função log(4/3)+1 e do termo "programacao" também. Assim, os valores recuperados já multiplicados pelos IDFs serão:

- introducao: [ (2; 0.562), (3; 0.562), (4; 0.281) ]
- programacao: [ (1; 0.562), (3; 0.562), (4; 0.562) ]  

Finalmente, geramos uma única lista com os valores que tenham o mesmo ID somados:

[ (1; 0.562), (2; 0.562), (3; 1.124), (4; 0.843) ]

Ao ordenarmos essa lista pelo valor de cada ID, teremos a seguinte lista: [3, 4, 1, 2]. Assim, os títulos dos livros serão apresentados nessa ordem para os usuários. Em um sistema real, geralmente as respostas são apresentadas de 10 em 10 (como no Google).

Neste projeto, vocês devem implementar o índice invertido e a busca exatamente como descrito acima, mas para o sistema do TP2. Lembre-se de que o índice invertido usará as listas invertidas cujo código está disponível

Links to an external site.. Nesse exemplo, elas devem ser atualizadas sempre que um livro for incluído, excluído ou tiver seu título alterado.

O QUE DEVE SER FEITO?

    Implementar o índice invertido 

    Links to an external site. para as séries, para os episódios e para os atores usando as palavras dos títulos e dos nomes (conforme a entidade).
    Implementar as buscas de séries, episódios e atores ordenando as respostas pelo valor TFxIDF.

Quanto tudo estiver pronto, a busca pelas entidades (séries, atores e episódios) deverá ser apenas pelo índice invertido.

RELATÓRIO

Vocês devem postar o seu trabalho no GitHub e enviar apenas o URL do seu projeto. Criem um repositório específico para este projeto (ao invés de mandar o repositório pessoal de algum de vocês em que estejam todos os seus códigos). Acrescentem um arquivo readme.md ao projeto que será o relatório do trabalho de vocês. Nele, descrevam um pouco o esforço. Mesmo que eu tenha acabado de especificar, acima, o que eu gostaria que fosse feito, eu gostaria de ver a descrição do seu trabalho nas suas próprias palavras. Basicamente, vocês devem responder à seguinte pergunta: O que o trabalho de vocês faz?

Em seguida, listem os nomes dos participantes e descrevam todas as classes criadas e os seus métodos principais. O objetivo é que vocês facilitem ao máximo a minha correção, de tal forma que eu possa entender com facilidade tudo aquilo que fizeram e dar uma nota justa.

Finalmente, relatem um pouco a experiência de vocês, explicando questões como: Vocês implementaram todos os requisitos? Houve alguma operação mais difícil? Vocês enfrentaram algum desafio na implementação? Os resultados foram alcançados? ... A ideia, portanto, é relatar como foi a experiência de desenvolvimento do TP. Aqui, a ideia é entender como foi para vocês desenvolver este TP.

Para concluir, vocês devem, necessariamente, responder ao seguinte checklist (copie as perguntas abaixo para o seu relatório e responda sim/não em frente a elas):

    O índice invertido com os termos dos títulos das séries foi criado usando a classe ListaInvertida?
    O índice invertido com os termos dos títulos dos episódios foi criado usando a classe ListaInvertida?
    O índice invertido com os termos dos nomes dos atores foi criado usando a classe ListaInvertida?
    É possível buscar séries por palavras usando o índice invertido?
    É possível buscar episódios por palavras usando o índice invertido?
    É possível buscar atores por palavras usando o índice invertido?
    O trabalho está completo?
    O trabalho é original e não a cópia de um trabalho de um colega?

Lembre-se de que, para essa atividade, eu avaliarei tanto o esforço quanto o resultado. Portanto, escreva o relatório de forma que me ajude a observar o resultado.

AVALIAÇÃO

Essa atividade vale 5 pontos. A avaliação será feita por meio do relatório. Dessa forma, um relatório incompleto ou ausente impactará na perda significativa de pontos na avaliação do projeto.

Atenção: Trabalhos copiados de colegas, que não evidenciem um esforço mínimo do próprio aluno, serão anulados.

Se tiver dúvidas sobre o trabalho a fazer, me avise. Não deixe de observar que o URL com o código no GitHub deve ser entregue até o dia especificado na atividade.