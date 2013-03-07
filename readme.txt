As seguintes pastas NAO devem estar no repositorio (sao geradas ou tem apenas ficheiros temporarios):
dist
doc/api       ; o seu conteudo e' gerado pelo javadoc
res           ; o eclipse coloca nesta pasta o resultado da sua compilacao
tmp-build

Ficheiros especificos do eclipse (que devem ser mantidos no repositorio):
.classpath
.project

Para executar o projecto fazer (no minimo):
$> cd scripts
$> ./cc.sh                    ; para gerar codigo java com base na especificacao das linguagens - ficheiros .g
$> ./makejar.sh               ; compila todo o codigo java; junta aos recursos (src-resources); gera o ficheiro csheets.jar e coloca o necessario na pasta dist 
$> ./run.sh                   ; executa o csheets.jar que se encontra na pasta dist


