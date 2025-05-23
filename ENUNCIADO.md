# RELACIONAMENTO N:N

### Os tipos de relacionamentos que nos interessam na disciplina são:

    1:N - Uma entidade de um tipo se relacionada com várias entidades de outro tipo. Esse é o caso mais comum, usado para categorias, grupos, etc. Exemplos: uma editora possui vários livros; uma categoria possui vários produtos; um trabalhador possui vários dependentes; etc. Esse tipo de relacionamento vocês já implementaram no TP1.
    N:N - As entidades de um tipo se relacionam com várias entidades de outro tipo. Exemplos: um ou mais autores escrevem um ou mais livros; um ou mais atores fazem um ou mais filmes; uma ou mais playlists contém uma ou mais músicas; etc. Esse tipo de relacionamento, vocês devem implementar neste TP2.

A implementação de relacionamentos N:N é um pouco mais complicada que o relacionamento 1:N, porque a gente precisa poder consultar as relações a partir de qualquer uma das duas entidades. Pensando de uma forma simplista, seria como se a gente tivesse que implementar dois relacionamentos 1:N entre essas entidades.

Vamos tomar como exemplo um relacionamento N:N entre filmes e atores. Nesse exemplo, temos os filmes Iniciação (ID: 1), Ruína (ID: 2) e Ascensão (ID: 3) e os atores Ana (ID: 1), Pedro (ID: 2) e João (ID: 3). A participação dos atores nos filmes será da seguinte forma:

    Ana (1) participou dos filmes Iniciação (1) e Ascensão (3).
    Pedro (2) participou dos filmes Iniciação (1), Ruína (2) e Ascensão (3).
    João (3) participou dos filmes Iniciação (1) e Ruína (2).

### Invertendo as relações, temos:

    Do filme Iniciação (1), participaram Ana (1), Pedro (2) e João (3).
    Do filme Ruína (2), participaram Pedro (2) e João (3).
    Do filme Ascensão (3), participaram Ana (1) e Pedro (2).

### Registramos essas participações por meio dessas duas associações de IDs:

|idAtor |	idFilme
|-------|----------
|1 | 	1
|1 | 	3
|2 |	1
|2 |	2
|2 |	3
|3 |	1
|3 |	2
	  	
|idFilme |	idAtor
|--------|--------
|1 |	1
|1 |	2
|1 |	3
|2 |	2
|2 |	3
|3 |	1
|3 |	3

A razão pela qual criamos duas estruturas para conter os relacionamentos é porque, geralmente, as estruturas de dados só permitem a busca eficiente por uma única chave. No caso de uma árvore B+, por exemplo, o critério de ordenação deve ser o ID do Filme OU o ID do Ator. Não dá para uma única árvore B+ ter dois critérios de ordenação.

Eventualmente, quando o relacionamento carrega dados próprios (ex: nome da personagem), seria importante termos uma terceira entidade no meio do caminho.  Aí, as relações acima seriam (idAtor; idPersonagem) e (idFilme; idPersonagem). 

Neste projeto, porém, você deve fazer criar um relacionamento N:N entre as séries e os atores. Um ator deve ser um objeto da classe Ator e conter pelo menos os atributos ID e nome e vocês precisarão implementar todo o CRUD de rótulos. Em seguida, nas operações com Séries, precisarão inserir e eliminar entradas nas duas árvores B+ que representam o relacionamento das tarefas com os rótulos. Vocês também precisarão manter a consistência em todo o processo de manutenção dos atores. Por exemplo, não deve ser possível excluir um ator que estiver vinculado a alguma série.

---------------

# PROGRAMA PRINCIPAL

### No programa principal, vocês vão implementar agora a terceira opção, por meio da qual serão realizadas as inclusões, alterações, buscas e exclusões de atores, como mostrado abaixo.

    PUCFlix 1.0
    -----------
    > Início

    1) Séries
    2) Episódios
    3) Atores
    0) Sair

### No menu interno de atores, ao mostrar os dados de um ator, deve ser possível visualizar as séries das quais eles participam. 

No entanto, a manutenção do vínculo entre séries e atores poderá ser feita dentro do menu de séries. Ao cadastrar uma série, vocês devem indicar os seus atores. Vocês podem organizar isso de duas formas: (1) ao incluir ou editar os dados de uma série, a lista de atores seria apenas uma das informações gerenciadas; (2) criar, no menu de séries, uma opção específica para a vinculação de atores às séries. Eventualmente, vocês podem criar uma quarta opção no menu principal chamada de Elenco (ou algo parecido).

Lembrem-se de que vocês precisarão de duas árvores B+: uma para o par (idSerie; idAtor) e outra para o par (idAtor; idSerie).

## O QUE DEVE SER FEITO?

    Implementar o CRUD de Atores.
    Implementar o relacionamento N:N entre Séries e Atores usando duas árvores B+.
    Criar a visão e o controle de atores. Assegurar que um ator não possa ser excluído se estiver vinculado a um filme.
    Ajustar a visão e o controle de séries para permitir a vinculação de atores.
    Assegurar a exclusão desses vínculos quando uma série for excluída.
    Na visão de séries, permitir a visualização dos atores de uma série.
    Na visão de atores, permitir a visualização das séries de um ator.

### Observe que para tudo funcionar, você precisará acessar os arquivos e as visões de tarefas e categorias em todas as classes de controle.

---------------

# FORMA DE ENTREGA

Vocês devem postar o seu trabalho no GitHub e enviar apenas o URL do seu projeto. Criem um repositório específico para este projeto (ao invés de mandar o repositório pessoal de algum de vocês em que estejam todos os seus códigos). Acrescentem um arquivo readme.md ao projeto que será o relatório do trabalho de vocês. Nele, descrevam um pouco o esforço. Mesmo que eu tenha acabado de especificar, acima, o que eu gostaria que fosse feito, eu gostaria de ver a descrição do seu trabalho nas suas próprias palavras. Basicamente, vocês devem responder à seguinte pergunta: O que o trabalho de vocês faz?

Em seguida, listem os nomes dos participantes e descrevam todas as classes criadas e os seus métodos principais. O objetivo é que vocês facilitem ao máximo a minha correção, de tal forma que eu possa entender com facilidade tudo aquilo que fizeram e dar uma nota justa.

Finalmente, relatem um pouco a experiência de vocês, explicando questões como: Vocês implementaram todos os requisitos? Houve alguma operação mais difícil? Vocês enfrentaram algum desafio na implementação? Os resultados foram alcançados? ... A ideia, portanto, é relatar como foi a experiência de desenvolvimento do TP. Aqui, a ideia é entender como foi para vocês desenvolver este TP.

### Para concluir, vocês devem, necessariamente, responder ao seguinte checklist (copie as perguntas abaixo para o seu relatório e responda sim/não em frente a elas):

    As operações de inclusão, busca, alteração e exclusão de atores estão implementadas e funcionando corretamente?
    O relacionamento entre séries e atores foi implementado com árvores B+ e funciona corretamente, assegurando a consistência entre as duas entidades?
    É possível consultar quais são os atores de uma série?
    É posssível consultar quais são as séries de um ator?
    A remoção de séries remove os seus vínculos de atores?
    A inclusão de um ator em uma série em um episódio se limita aos atores existentes?
    A remoção de um ator checa se há alguma série vinculado a ele?
    O trabalho está funcionando corretamente?
    O trabalho está completo?
    O trabalho é original e não a cópia de um trabalho de outro grupo?

### Lembre-se de que, para essa atividade, eu avaliarei tanto o esforço quanto o resultado. Portanto, escrevam o relatório de forma que me ajude a observar o resultado.

---------------

# AVALIAÇÃO

Essa atividade vale 5 pontos. A avaliação será feita por meio do relatório. Dessa forma, um relatório incompleto ou ausente impactará na perda significativa de pontos na avaliação do projeto.

### Atenção: Trabalhos copiados de colegas, que não evidenciem um esforço mínimo do próprio aluno, serão anulados.

Se tiver dúvidas sobre o trabalho a fazer, me avise. Não deixe de observar que o URL com o código no GitHub deve ser entregue até o dia especificado na atividade.