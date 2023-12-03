import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.*;
import java.util.Scanner;

public class Request {

    private static void putRequest(String myUrlString) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        URL myUrl = new URL(myUrlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(myUrl.toURI())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());

    }



    private static void postRequest(String myUrlString, String myBody) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        URL myUrl = new URL(myUrlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(myUrl.toURI())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(myBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());

    }



    private static void getRequest(String myUrlString) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        URL myUrl = new URL(myUrlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(myUrl.toURI())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());

    }



    private static void deleteRequest(String myUrlString) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        URL myUrl = new URL(myUrlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(myUrl.toURI())
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());

    }



    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        //VARIABILI CHE SOSTITUISCONO GLI INPUT DELL'UTENTE (per risparmiare tempo)
        String urlStringPut = "http://localhost:8080/api/cane/generate";

        String urlStringPostDog = "http://localhost:8080/api/cane/";
        String bodyPostDog = "{\"nome\":\"Bobby4\", \"padrone\":{\"nome\":\"iper-nome\", \"cognome\":\"iper-cognome\", \"indirizzo\":{\"viaENumero\":\"vietta e numerello\", \"cap\":\"03011\", \"citta\":{\"nome\":\"alatri\", \"codiceIstat\":\"code\", \"codiceCatastale\":\"another code\"}}, \"titoliDiStudio\":[]}}";
        String urlStringGetDog = "http://localhost:8080/api/cane/";
        String urlStringDeleteDog = "http://localhost:8080/api/cane/141";

        String urlStringPostPerson = "http://localhost:8080/api/persona/";
        String bodyPostPerson = "{\"nome\":\"super-nome\", \"cognome\":\"super-cognome\", \"indirizzo\":{\"viaENumero\":\"viona e numerone\", \"cap\":\"03011\", \"citta\":{\"nome\":\"alatri\", \"codiceIstat\":\"istat\", \"codiceCatastale\":\"catasto\"}}, \"titoliDiStudio\":[]}";
        String urlStringGetPerson = "http://localhost:8080/api/persona/";
        String urlStringDeletePerson = "http://localhost:8080/api/persona/142";

        System.out.println("HI! THIS IS SPRING TEST.");
        //istanziazione degli oggetti necessari
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("\n\nWhat should I do for you?");
            System.out.println("1) Put request");
            System.out.println("2) Post dog request");
            System.out.println("3) Get dog request");
            System.out.println("4) Delete dog request");
            System.out.println("5) Post person request");
            System.out.println("6) Get person request");
            System.out.println("7) Delete person request");
            System.out.println("8) Quit");

            char selectedCommand = scanner.next().charAt(0);

            switch(selectedCommand) {
                case '1':
                    putRequest(urlStringPut);
                    break;

                case '2':
                    postRequest(urlStringPostDog, bodyPostDog);
                    break;

                case '3':
                    getRequest(urlStringGetDog);
                    break;

                case '4':
                    deleteRequest(urlStringDeleteDog);
                    break;

                case '5':
                    postRequest(urlStringPostPerson, bodyPostPerson);
                    break;

                case '6':
                    getRequest(urlStringGetPerson);
                    break;

                case '7':
                    deleteRequest(urlStringDeletePerson);
                    break;

                case '8':
                    System.out.println("BYE!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Sorry, the provided input is incorrect. Please try again.");
                    break;

            }

        }

    }

}
