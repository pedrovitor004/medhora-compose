# Persistência local no MedHora

## O que mudou

Antes, listas e mapas em memória armazenavam medicamentos, doses e opções de lembretes. Quando o Android encerrava o processo, esses dados desapareciam.

Agora, o Room grava os medicamentos e as doses tomadas no arquivo `medhora.db`. O DataStore Preferences grava as configurações no arquivo `datastore/preferencias_medhora.preferences_pb`. Ambos ficam no armazenamento privado do aplicativo e funcionam sem internet.

Fechar o app ou encerrar seu processo não apaga esses arquivos. Limpar o armazenamento do app ou desinstalá-lo pode apagar os dados. Não existe migração dos antigos dados em memória, pois eles não eram gravados em arquivo.

## 1. Dependências e geração de código

- `gradle/libs.versions.toml`: versões e aliases do Room, DataStore e KSP.
- `app/build.gradle.kts`: dependências e configuração de exportação do esquema do Room para `app/schemas`.
- KSP: processa as anotações do Room e gera a implementação do banco/DAO durante a compilação.

## 2. Entities: as tabelas

Em `data/local/Entidades.kt`:

- `MedicamentoEntity` representa a tabela `medicamentos`. O `id` identifica cada medicamento.
- `DoseEntity` representa `doses_tomadas`. A chave composta de medicamento, data e horário impede duplicar a mesma dose.

A dose guarda nome, dosagem e cor como eram quando ela foi registrada. Assim, editar ou excluir o medicamento mantém o histórico anterior. Os métodos `paraEntidade` e `paraModelo` separam a estrutura do banco dos modelos utilizados pelas telas.

## 3. Conversores de tipos

Em `data/local/Conversores.kt`, `@TypeConverter` transforma datas, horas e listas de horários em texto e faz o caminho inverso. Por exemplo, um `LocalDate` vira `2026-11-03`. Os enums são armazenados pelo Room com seus nomes.

## 4. DAO: as operações sobre os dados

Em `data/local/MedHoraDao.kt`:

- `@Query`: lê ou exclui dados usando SQL.
- `@Insert`: cadastra medicamento e registra dose tomada. No fluxo atual, registrarDoseSeAusente usa IGNORE para preservar a primeira confirmação quando a mesma chave chegar novamente; registrarDose, com REPLACE, permite substituir um registro.
- `@Update`: atualiza o medicamento existente.
- `@Transaction`: limpa as duas tabelas em uma única operação atômica.
- `Flow`: emite as listas novamente quando o banco muda.
- `suspend`: permite aguardar operações assíncronas. A palavra-chave sozinha não cria uma thread; nestes DAOs, o Room executa o acesso ao banco fora da thread da interface.

## 5. Database: o banco de dados

Em `data/local/MedHoraDatabase.kt`, `@Database` declara as tabelas e a versão inicial (`1`). A Application cria uma instância compartilhada com `Room.databaseBuilder`.

Quando a estrutura das tabelas mudar no futuro, será necessário aumentar a versão e implementar uma migração. Não foi configurada destruição automática do banco como alternativa a migrações.

## 6. Repositório e interface

`MedicamentoRepository` transforma as entidades em modelos e combina as consultas observáveis em um `StateFlow`. O banco é a fonte dos dados; a lista observada em memória é apenas o estado atual para desenhar as telas.

`MedHoraNavHost` observa o fluxo com `collectAsState`. Cadastro, edição, exclusão e marcação de dose usam corrotinas. A navegação após salvar só ocorre quando a escrita termina. Falhas de gravação mostram uma mensagem na tela.

Os horários futuros são calculados a partir dos medicamentos. Apenas as doses efetivamente tomadas são gravadas na tabela de histórico. Desmarcar uma dose remove esse registro.

## 7. DataStore Preferences: chave e valor

Em `PreferenciasRepository.kt`, `preferencesDataStore` garante uma instância compartilhada para o arquivo. As chaves booleanas são `lembretes_ativados`, `notificacoes_ativadas` e `som_ativado`.

- `store.data` fornece um `Flow` com os valores salvos.
- `booleanPreferencesKey` define uma chave booleana.
- `store.edit` grava as mudanças de forma transacional.
- Quando ainda não há valores, os padrões são lembretes desligados e notificações e som habilitados.

A preferência salva não substitui a permissão do sistema: o Android também precisa permitir notificações. DataStore é um complemento para configurações pequenas; a implementação exigida com Room está nos medicamentos e nas doses tomadas.

## 8. Inicialização e alarmes

`MedHoraApplication` inicializa os repositórios, aguarda a primeira leitura de Room e DataStore e então libera a interface. Alterações nos dados ou nas preferências atualizam os agendamentos.

`LembreteReceiver` usa `goAsync()` para esperar essa leitura antes de consultar o medicamento. Isso é necessário porque um alarme pode iniciar um processo novo, sem os antigos objetos em memória.

## Como demonstrar a persistência

1. Instale e abra o app; cadastre um medicamento com dois horários.
2. Marque uma dose como tomada e consulte o histórico.
3. Ative os lembretes em Configurações e conceda a permissão do Android.
4. Feche o app e encerre seu processo (ou use “Forçar parada” nas configurações do Android).
5. Abra novamente: confira medicamento, horários, histórico e opção de lembretes.
6. Edite o medicamento e repita o fechamento/reabertura.
7. Exclua o medicamento, reabra e confira que ele foi removido e que o histórico da dose continua.
8. Selecione o som “Silencioso” e reabra para conferir a seleção.
9. Desative lembretes e reabra para comprovar que o valor `false` também foi salvo.

“Forçar parada” serve para testar o armazenamento. Para testar a entrega de alarmes, reabra o aplicativo depois: o Android restringe alarmes de aplicativos forçados a parar.

## Visualizar os dados no Android Studio

Execute o app em um emulador/dispositivo com API 26 ou superior. Abra **View > Tool Windows > App Inspection > Database Inspector**, selecione o processo do MedHora e o banco `medhora.db`. Inspecione `medicamentos` e `doses_tomadas` ou execute `SELECT * FROM medicamentos`.

O arquivo do DataStore não é uma tabela SQL e não aparece no Database Inspector. Ele fica em `files/datastore` no armazenamento privado do app. Para a apresentação, demonstre os valores pela tela de configurações e mostre as chaves no código.

## Verificações automatizadas

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

PersistenciaLocalTest usa arquivos temporários: fecha e reabre o banco e recria o DataStore para verificar CRUD, histórico, desmarcação e preferências. As operações desse teste são isoladas, mas o executor de instalação é outra etapa: nesta configuração, connectedDebugAndroidTest removeu o aplicativo durante a limpeza. Execute testes instrumentados em um emulador de testes ou use instalação e instrumentação diretas sem desinstalar o aplicativo. Não use o executor conectado sobre cadastros importantes sem backup.

## Roteiro para a apresentação de 15 minutos

- 0–2 min: problema resolvido pelo MedHora e perda dos dados na versão anterior.
- 2–6 min: demonstração de cadastro, dose tomada e reabertura do aplicativo.
- 6–10 min: Entities, DAO, Database e conversores; mostrar as tabelas no Inspector.
- 10–13 min: DataStore, chaves, leitura com Flow e gravação com edit.
- 13–15 min: integração com Compose e alarmes, testes e perguntas.

Referências: [Room](https://developer.android.com/training/data-storage/room), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore).
