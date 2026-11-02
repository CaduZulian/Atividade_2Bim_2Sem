# Atividade_2Bim_2Sem - Consulta Fácil

Aplicativo Android desenvolvido em Kotlin como atividade prática da disciplina
de **Programação para Disp. Móveis II**, para identificação e consulta de
produtos por código de barras. O aplicativo permite digitar ou escanear um
código, consultar os dados do produto na API do Open Food Facts e visualizar
informações como nome, marca, quantidade, ingredientes e tabela nutricional.
As consultas realizadas ficam disponíveis no histórico do aplicativo.

<div align="center">
<img src=".github\project-image.png" alt="Consulta Fácil"/><br />
</div>

## Funcionalidades

- Consulta de produtos por código de barras digitado.
- Leitura de códigos de barras e QR Codes usando a câmera do dispositivo.
- Validação de códigos com 8 a 14 dígitos.
- Consulta de dados na API pública do [Open Food Facts](https://world.openfoodfacts.org/).
- Exibição de nome, marca, quantidade, ingredientes, imagem e informações nutricionais.
- Armazenamento local das consultas realizadas.
- Visualização do histórico e dos detalhes de cada produto.
- Tratamento de produto não encontrado, ausência de conexão e dados incompletos.

## Ferramentas utilizadas

- **[Android Studio](https://developer.android.com/studio)**
- **[Kotlin](https://kotlinlang.org/)**
- **[Android SDK](https://developer.android.com/)**
- **[AndroidX](https://developer.android.com/jetpack/androidx)**
- **[Material Components](https://m3.material.io/)**
- **[Gradle](https://gradle.org/)**
- **[ZXing Android Embedded](https://github.com/journeyapps/zxing-android-embedded)** - leitura de códigos de barras e QR Codes
- **[Open Food Facts API](https://world.openfoodfacts.org/data)** - fonte dos dados dos produtos

> Consulte o arquivo [app/build.gradle.kts](app/build.gradle.kts) para ver as
> dependências e configurações do módulo do aplicativo.

## Requisitos

Para executar o projeto, é necessário ter:

- **Android Studio** instalado.
- Android SDK com a API 37 instalada.
- JDK 11 ou compatível com a configuração do projeto.
- Um dispositivo Android ou um emulador com Android 7.0 (API 24) ou superior.
- Acesso à internet para consultar os produtos no Open Food Facts.

Para testar a leitura de códigos, o dispositivo precisa permitir o acesso à
câmera. Emuladores podem exigir uma webcam configurada ou o uso da entrada
manual de códigos.

## Permissões

O aplicativo solicita as seguintes permissões:

- `INTERNET` - necessária para consultar a API do Open Food Facts.
- `CAMERA` - necessária para ler códigos de barras e QR Codes.

## Estrutura principal

- `app/src/main/java/.../MainActivity.kt` - tela principal e entrada do código.
- `app/src/main/java/.../CustomScannerActivity.kt` - leitura de códigos pela câmera.
- `app/src/main/java/.../ApiService.kt` - validação do código e integração com a API.
- `app/src/main/java/.../DetalhesActivity.kt` - exibição dos dados do produto.
- `app/src/main/java/.../HistoricoActivity.kt` - listagem das consultas realizadas.
- `app/src/main/java/.../DatabaseHelper.kt` e `ConsultaDao.kt` - persistência local do histórico.
- `app/src/main/res/layout/` - layouts das telas e dos itens exibidos no aplicativo.

## Rodando o projeto (desenvolvimento)

O projeto deve ser aberto e executado pelo **Android Studio**:

1. Clone este repositório:

   ```bash
   git clone https://github.com/CaduZulian/Atividade_2Bim_2Sem.git
   ```

2. Abra o Android Studio e selecione **Open**.
3. Escolha a pasta clonada do projeto.
4. Aguarde a sincronização do Gradle e instale os componentes do Android SDK
   solicitados pelo IDE.
5. Selecione um dispositivo físico ou crie/inicie um emulador.
6. Clique em **Run** ou use `Shift + F10` para executar o aplicativo.

Também é possível executar a compilação pelo terminal do Windows:

```powershell
.\gradlew.bat assembleDebug
```

O APK de debug será gerado em
`app\build\outputs\apk\debug\app-debug.apk`.

## Rodando os testes

Os testes unitários podem ser executados pelo Android Studio ou pelo terminal:

```powershell
.\gradlew.bat test
```

Os testes verificam principalmente a validação de códigos, o processamento das
respostas da API e a conversão dos dados de produtos para o histórico.

## Gerando a versão de produção

Para gerar o APK de release pelo terminal do Windows:

```powershell
.\gradlew.bat assembleRelease
```

O arquivo será gerado em
`app\build\outputs\apk\release\app-release-unsigned.apk`.

> A versão de release gerada por esse comando não possui assinatura de
> distribuição. Para publicar ou instalar em determinados dispositivos, é
> necessário configurar uma chave de assinatura no Android Studio.

## API utilizada

As consultas são realizadas no endpoint:

```text
https://world.openfoodfacts.org/api/v2/product/{codigo}.json
```

O aplicativo usa o código informado para buscar o produto e trata os campos
que podem não estar disponíveis na base, exibindo valores indicativos quando
faltarem informações.

## Desenvolvedores

<table align="center">
<tr>
<td>
<div align="center">
<img style="width: 150px; border-radius: 50%;" src="https://github.com/CaduZulian.png" alt="CaduZulian"/><br />
<a href="https://github.com/CaduZulian">Carlos Eduardo Zulian</a>
</div>
</td>
</tr>
</table>
